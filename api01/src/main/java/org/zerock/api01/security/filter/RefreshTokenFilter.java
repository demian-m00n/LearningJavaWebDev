package org.zerock.api01.security.filter;

import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerock.api01.security.exception.RefreshTokenException;
import org.zerock.api01.util.JWTUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {
    private final String refreshPath;

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException , IOException{
        String path = request.getRequestURI();

        if (!path.equals(refreshPath)){
            log.info("skip refresh token filter......");
            filterChain.doFilter(request,response);
            return;
        }

        log.info("Refresh Token Filter...run............");

        //JSON 에서 accessToken Refresh Token catch
        Map<String,String> tokens = parseRequestJSON(request);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        log.info("accessToken : " + accessToken);
        log.info("refreshTokne : "+refreshToken);

        try {
            checkAccessToken(accessToken);
        } catch (RefreshTokenException refreshTokenException){
            refreshTokenException.sendResponseError(response);
        }

        Map<String,Object> refreshClaims = null;

        try {
            refreshClaims = checkRefreshToken(refreshToken);
            log.info(refreshClaims);

            Integer exp = (Integer) refreshClaims.get("exp");

            Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli()*1000);
            Date current = new Date(System.currentTimeMillis());

            //만료 시간과 현재 시간 차이 계산
            //3일 미만인 경우에는 Refresh Token 도 재발급
            long gapTime = (expTime.getTime() - current.getTime());

            log.info("-------------------------------------------");
            log.info("current : "+current);
            log.info("expTime : "+expTime);
            log.info("gap : "+gapTime);

            String mid = (String) refreshClaims.get("mid");

            //이 상태까지 오면 무조건 AccessToken 은 새로 생성
            String accessTokenValue = jwtUtil.generateToken(Map.of("mid",mid),1);
            String refreshTokenValue = tokens.get("refreshToken");

            //Refresh Token 3일 미만이라면
            if (gapTime < (1000* 60 * 60 * 24 * 3)){
                log.info("new Refresh Token required.............");
                refreshTokenValue = jwtUtil.generateToken(Map.of("mid",mid),30);
            }

            log.info("Refresh Token result..............................");
            log.info("accessToken : " + accessTokenValue);
            log.info("refreshToken : "+ refreshTokenValue);

            sendTokens(accessTokenValue,refreshTokenValue,response);
        } catch (RefreshTokenException refreshTokenException){
            refreshTokenException.sendResponseError(response);
            return;
        }
    }

    private Map<String,String> parseRequestJSON(HttpServletRequest request){

        //JSON 분석 후 mid, mpw 전달 값을 Map 으로 처리
        try (Reader reader = new InputStreamReader(request.getInputStream())){
            Gson gson = new Gson();

            return gson.fromJson(reader,Map.class);
        } catch (Exception e){
            log.error(e.getMessage());
        }

        return null;
    }

    private void checkAccessToken(String accessToken) throws RefreshTokenException {
        try {
            jwtUtil.validateToken(accessToken);
        } catch (ExpiredJwtException expiredJwtException){
            log.info("Access Token has expired");
        }catch (Exception e){
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_ACCESS);
        }
    }

    private Map<String ,Object> checkRefreshToken(String refreshToken) throws
            RefreshTokenException {
        try {
            Map<String,Object> values = jwtUtil.validateToken(refreshToken);
            return values;
        } catch (ExpiredJwtException expiredJwtException){
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);
        } catch (MalformedJwtException malformedJwtException){
            log.error("MalformedJwtException--------------------------");
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        } catch (Exception e){
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }
    }

    private void sendTokens(String accessTokenValue, String refreshTokenValue,
                            HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        String jsonStr = gson.toJson(Map.of("accessToken",accessTokenValue,
                "refreshToken",refreshTokenValue));

        try {
            response.getWriter().println(jsonStr);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}

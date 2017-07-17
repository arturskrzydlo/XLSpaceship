package com.xebia.services.game;

import com.xebia.dto.GameCreatedDTO;
import com.xebia.dto.PlayerDTO;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Created by artur.skrzydlo on 2017-07-05.
 */

@Aspect
@Component
public class NewGameAspect {


    Logger logger = LoggerFactory.getLogger(NewGameAspect.class);

    @Pointcut("execution(* com.xebia.services.game.GameServiceClient.challengePlayerForAGame(..)) ")
    public void doLog() {

        logger.info("========= DO LOG");
    }


    @Before("com.xebia.services.game.NewGameAspect.doLog() && args(playerDTO)")
    public void aroundPlayerChallenge(PlayerDTO playerDTO) {

        logger.info(" ========== [ASPECT] Challenging player on host : " + playerDTO.getSpaceshipProtocol().getHostname());

    }

    @AfterReturning(value = "com.xebia.services.game.NewGameAspect.doLog()", returning = "gameCreatedDTO")
    public void returnedPlayer(GameCreatedDTO gameCreatedDTO) {

        logger.info(" ========== [ASPECT] Player with name " + gameCreatedDTO.getFullName() + " challenged !");
    }
}

package com.xebia.util;

import com.xebia.dto.PlayerDTO;
import com.xebia.dto.SpaceshipProtocolDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Component
public class OwnerUtil {

    public static String NAME;
    public static String FULL_NAME;
    public static String HOST;
    public static Integer PORT;

    private static PlayerDTO simulationUser;

    public static PlayerDTO getSimulationUser() {
        simulationUser = new PlayerDTO();
        simulationUser.setUserId(NAME);
        simulationUser.setFullName(FULL_NAME);
        simulationUser.setSpaceshipProtocol(new SpaceshipProtocolDTO(HOST, PORT));
        return simulationUser;
    }

    public static void setSimulationUser(PlayerDTO simulationUser) {
        OwnerUtil.simulationUser = simulationUser;
    }

    @Value("${player.name}")
    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    @Value("${player.fullname}")
    public void setFULL_NAME(String FULL_NAME) {
        this.FULL_NAME = FULL_NAME;
    }

    @Value("${player.hostname}")
    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    @Value("${player.port}")
    public void setPORT(Integer PORT) {
        this.PORT = PORT;
    }
}

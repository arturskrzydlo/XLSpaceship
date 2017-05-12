package com.xebia.util;

import com.xebia.dto.PlayerDTO;
import com.xebia.dto.SpaceshipProtocolDTO;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public class OwnerUtil {


    public static final String HOST = "localhost";
    public static final Integer PORT = 8080;
    public static final String NAME = "artur";
    public static final String FULL_NAME = "artur skrzydlo";

    private static PlayerDTO simulationUser;

    public static PlayerDTO getSimulationUser() {
        //TODO: creation of user player according to startup configuration

        simulationUser = new PlayerDTO();
        simulationUser.setUserId(NAME);
        simulationUser.setFullName(FULL_NAME);
        simulationUser.setSpaceshipProtocol(new SpaceshipProtocolDTO(HOST, PORT));
        return simulationUser;
    }

    public static void setSimulationUser(PlayerDTO simulationUser) {
        OwnerUtil.simulationUser = simulationUser;
    }
}

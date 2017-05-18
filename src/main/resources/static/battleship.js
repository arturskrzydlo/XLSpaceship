// Player data
var hostname;
var port;
var userId;

// set grid rows and columns and the size of each square
var squareSize = 30;
var gameSize = 16;
var gameId;
var availableGamesSize = 0;
var bothBoardsUpdate = false;

// get the container element
var ownerGameBoardContainer = $("#ownerGameBoard");
var opponentGameBoardContainer = $("#opponentGameBoard");
var firedSalvo = {

    salvo: []
};

var isAutopilotOn = false;

$(document).ready(function () {

    initializeSimulatorOwner();

    drawGameBoard(gameSize, ownerGameBoardContainer);
    drawGameBoard(gameSize, opponentGameBoardContainer);

    $("#selectGame").change(gameChange);

    $("#startGameForm").submit(function () {
        clearError();
        event.preventDefault();
        var protocol = createSpaceshipProtocol();
        if (protocol != undefined) {

            $.ajax({
                type: "POST",
                dataType: "json",
                data: JSON.stringify(protocol),
                contentType: "application/json; charset=utf-8",
                url: "http://" + hostname + ":" + port + "/xl-spaceship/user/game/new",
                success: function (gameStatus) {
                    clearGameBoards();
                    setupGame(gameStatus);
                },
                error: function (error) {
                    handleError(error.responseJSON);
                }
            })
        }
    });


    $("#checkGameBtn").click(function () {

        $.ajax({
            type: "GET",
            dataType: "json",
            url: "http://" + hostname + ":" + port + "/xl-spaceship/user/game/" + gameId,
            success: function (gameStatus) {
                createGameBoards(gameStatus, bothBoardsUpdate);
                bothBoardsUpdate = false;
                $("#opponentPlayerName h4").text(gameStatus.opponent.user_id);

                if (gameStatus.game.won != undefined) {
                    if (gameStatus.game.won === userId) {
                        $("#ownPlayerName h4").text(gameStatus.self.user_id + "  -WINNER !");
                        $("#ownPlayerName h4").css("color", "darkred");
                    } else {
                        $("#opponentPlayerName h4").text(gameStatus.opponent.user_id + "  -WINNER !");
                        $("#opponentPlayerName h4").css("color", "darkred");
                    }

                }
            },
            error: function (error) {
                handleError(error.responseJSON);
            }
        })
    });

    $("#fireSalvo").click(function () {
        clearError();

        $.ajax({
            type: "PUT",
            dataType: "json",
            data: JSON.stringify(firedSalvo),
            contentType: "application/json; charset=utf-8",
            url: "http://" + hostname + ":" + port + "/xl-spaceship/user/game/" + gameId + "/fire",
            success: function (salvoResult) {
                updateGameBoardAfterSalvo(salvoResult);
                if (isAutopilotOn) {
                    $("#fireSalvo").attr("disabled", "disabled");
                }
            },
            error: function (error) {
                handleError(error.responseJSON);
            }
        })
    });

    $("#autopilot").click(function () {
        clearError();

        $.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            url: "http://" + hostname + ":" + port + "/xl-spaceship/user/game/" + gameId + "/auto",
            success: function (salvoResult) {
                isAutopilotOn = true;
            },
            error: function (error) {
                handleError(error.responseJSON);
            }
        })
    });

    setInterval(checkFormAvailableGames, 3000);
    setInterval(autoUpdatedOwnBoard, 3000);
});

function createSpaceshipProtocol() {
    var portFromInput = $("#port").val();
    var hostnameFromInput = $("#hostname").val();

    if (portFromInput == port && hostnameFromInput == hostnameFromInput) {
        handleError({message: "Can'n challenge yourself. Hostname and port are equal to yours"});
        return;
    }
    var spaceship_protocol = {

        spaceship_protocol: {
            hostname: hostnameFromInput,
            port: portFromInput

        }
    }

    return spaceship_protocol;
}

function handleError(responeJSON) {
    var message = responeJSON.message.split("message:");
    if (message.length > 1) {
        $("#errorMessages").text(message[1].slice(0, -1));
    }
    else {
        $("#errorMessages").text(message);
    }
    $("#errorMessages").css("display", "block");

}

function clearError() {
    $("#errorMessages").text("");
    $("#errorMessages").css("display", "none");
}

function gameChange() {
    if (gameId !== this.value) {
        bothBoardsUpdate = true;
        gameId = this.value;
        clearGameBoards();
        $("#checkGameBtn").click();
        clearSalvoMarks();
    }
}

function initializeSimulatorOwner() {

    $.ajax({
        type: "GET",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        url: "/xl-spaceship/user/data",
        success: function (player) {

            port = player.spaceship_protocol.port;
            hostname = player.spaceship_protocol.hostname;
            userId = player.user_id;

            $("#ownPlayerName h4").text(userId);
        },
        error: function (error) {
            handleError(error.responseJSON);
        }
    })
}
//create new or updated gameboard
// cornflowerblue : MY SHIP
// firebrick : HIT
// darkolivegreen : MISS
// #f6f8f9 : NOT_FIRED_YET

function createGameBoards(gameStatus, both) {

    var board = gameStatus.self.board;
    for (i = 0; i < gameSize; i++) {
        var shotsInRow = board[i].split("");
        for (j = 0; j < gameSize; j++) {
            switch (shotsInRow[j]) {

                case "*" :
                    ownerGameBoardContainer.find("#field" + i.toString(16) + j.toString(16)).css("background", "cornflowerblue");
                    break;
                case "X" :
                    ownerGameBoardContainer.find("#field" + i.toString(16) + j.toString(16)).css("background", "firebrick");
                    break;
                case "-" :
                    ownerGameBoardContainer.find("#field" + i.toString(16) + j.toString(16)).css("background", "darkolivegreen");
                    break;
                case "." :
                    break;
                default :
            }
        }
    }

    if (both || isAutopilotOn) {
        board = gameStatus.opponent.board;
        for (i = 0; i < gameSize; i++) {
            var shotsInRow = board[i].split("");
            for (j = 0; j < gameSize; j++) {
                switch (shotsInRow[j]) {

                    case "*" :
                        opponentGameBoardContainer.find("#field" + i.toString(16) + j.toString(16)).css("background", "cornflowerblue");
                        break;
                    case "X" :
                        opponentGameBoardContainer.find("#field" + i.toString(16) + j.toString(16)).css("background", "firebrick");
                        break;
                    case "-" :
                        opponentGameBoardContainer.find("#field" + i.toString(16) + j.toString(16)).css("background", "darkolivegreen");
                        break;
                    case "." :
                        break;
                    default :
                }
            }
        }

    }

}


function updateGameBoardAfterSalvo(salvoResult) {

    //clear salvo
    firedSalvo.salvo = [];

    var salvo = salvoResult.salvo;
    for (key in salvo) {
        if (salvo.hasOwnProperty(key)) {

            var splitted = key.split("x");
            var row = splitted[0];
            var column = splitted[1];

            switch (salvo[key]) {
                case "HIT" :
                case "KILL":
                    opponentGameBoardContainer.find("#field" + row + column).css("background", "firebrick");
                    break;
                case "MISS" :
                    opponentGameBoardContainer.find("#field" + row + column).css("background", "darkolivegreen");
                    break;
                default :
            }
        }
    }
}

function clearSalvoMarks() {

    $.each(firedSalvo.salvo, function (index, value) {
        var splitted = value.split("x");
        opponentGameBoardContainer.find("#field" + splitted[0] + splitted[1]).css("background", "#f6f8f9");
    });

    firedSalvo.salvo = [];

}

function checkFormAvailableGames() {

    $.ajax({
        type: "GET",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        url: "http://" + hostname + ":" + port + "/xl-spaceship/user/game/all",
        success: function (game) {

            if (availableGamesSize !== game.length) {

                availableGamesSize = game.length;

                $("#selectGame").empty();
                $.each(game, function (index, value) {
                    $("#selectGame").append('<option value="' + value.game_id + '">' + value.game_id + '</option>');
                });

                if (game.length == 1) {
                    gameId = game[0].game_id;
                    $("#checkGameBtn").click();
                }
            }

        },
        error: function (error) {
            handleError(error.responseJSON);
        }
    })

}

function autoUpdatedOwnBoard() {

    if (gameId !== undefined) {
        $("#checkGameBtn").click();
    }
}

function setupGame(gameStatus) {
    $("#opponentPlayerName h4").text(gameStatus.full_name);
    gameId = gameStatus.game_id;
    $("#checkGameBtn").click();
    availableGamesSize++;
    $("#selectGame").append('<option value="' + gameId + '">' + gameId + '</option>');
    $("#selectGame").val(gameId);
    clearGameBoards()
    clearSalvoMarks();

}
function clearGameBoards() {

    for (i = 0; i < gameSize; i++) {
        for (j = 0; j < gameSize; j++) {
            opponentGameBoardContainer.find("#field" + j.toString(16) + i.toString(16)).css("background", "#f6f8f9");
            ownerGameBoardContainer.find("#field" + j.toString(16) + i.toString(16)).css("background", "#f6f8f9");
        }
    }
}
// make the grid columns and rows
function drawGameBoard(size, player) {
    for (i = 0; i < size; i++) {
        for (j = 0; j < size; j++) {

            var square = $("<div>");
            // unique id for each field
            square.attr("id", "field" + j.toString(16) + i.toString(16));

            // set each grid square's coordinates: multiples of the current row or column number
            var topPosition = j * squareSize;
            var leftPosition = i * squareSize;

            // use CSS absolute positioning to place each grid square on the page
            square.css("top", topPosition + 'px');
            square.css("left", leftPosition + 'px');
            player.append(square);
        }
    }
}


// set event listener for all elements in gameboard, run markShots function when square is clicked
opponentGameBoardContainer.click(markShot);

function markShot(e) {
    //no the parent element on which the event listener was set
    if (e.target !== e.currentTarget) {

        var row = e.target.id.substring(5, 6);
        var col = e.target.id.substring(6, 7);

        var index = firedSalvo.salvo.indexOf(row + "x" + col);
        if (index > -1) {
            $(e.target).css("background", "#f6f8f9");
            firedSalvo.salvo.splice(index, 1);
        } else {
            if ($(e.target).css("background-color") == "rgb(246, 248, 249)") {
                $(e.target).css("background", "darkorange");
                firedSalvo.salvo.push(row + "x" + col);
            }
        }

    }
    e.stopPropagation();
}

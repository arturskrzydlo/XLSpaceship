// set grid rows and columns and the size of each square
var squareSize = 30;
var gameSize = 16;
var gameId = 1;

// get the container element
var ownerGameBoardContainer = $("#ownerGameBoard");
var opponentGameBoardContainer = $("#opponentGameBoard");
var firedSalvo = {

    salvo: []
};

$(document).ready(function () {

    drawGameBoard(gameSize, ownerGameBoardContainer);
    drawGameBoard(gameSize, opponentGameBoardContainer);

    $("#startGameForm").submit(function () {
        clearError();
        event.preventDefault();
        var protocol = createSpaceshipProtocol();
        $.ajax({
            type: "POST",
            dataType: "json",
            data: JSON.stringify(protocol),
            contentType: "application/json; charset=utf-8",
            url: "http://localhost:8080/xl-spaceship/user/game/new",
            success: function (gameStatus) {
                setupGame(gameStatus);
            },
            error: function (error) {
                handleError(error.responseJSON);
            }
        })
    });


    $("#checkGameBtn").click(function () {
        clearError();

        $.ajax({
            type: "GET",
            dataType: "json",
            url: "http://localhost:8080/xl-spaceship/user/game/" + gameId,
            success: function (gameStatus) {
                createGameBoard(gameStatus);
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
            url: "http://localhost:8080/xl-spaceship/user/game/" + gameId + "/fire",
            success: function (salvoResult) {
                updateGameBoardAfterSalvo(salvoResult);
            },
            error: function (error) {
                handleError(error.responseJSON);
            }
        })
    });
});

function createSpaceshipProtocol() {
    var portFromInput = $("#port").val();
    var hostnameFromInput = $("#hostname").val();

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
    $("#errorMessages").text(message[1]);
    $("#errorMessages").css("display", "inline");

}

function clearError() {
    $("#errorMessages").text("");
    $("#errorMessages").css("display", "none");
}
//create new or updated gameboard
// cornflowerblue : MY SHIP
// firebrick : HIT
// darkolivegreen : MISS
// #f6f8f9 : NOT_FIRED_YET

function createGameBoard(gameStatus) {

    var board = gameStatus.self.board;
    for (i = 0; i < gameSize; i++) {
        var shotsInRow = board[i].split("");
        for (j = 0; j < gameSize; j++) {
            switch (shotsInRow[j]) {

                case "*" :
                    ownerGameBoardContainer.find("#field" + i + j).css("background", "cornflowerblue");
                    break;
                case "X" :
                    ownerGameBoardContainer.find("#field" + i + j).css("background", "firebrick");
                    break;
                case "-" :
                    ownerGameBoardContainer.find("#field" + i + j).css("background", "darkolivegreen");
                    break;
                case "." :
                    break;
                default :
            }
        }
    }

}


function updateGameBoardAfterSalvo(salvoResult) {

    var salvo = salvoResult.salvo;
    for (i = 0; i < salvo.size; i++) {

        for (j = 0; j < gameSize; j++) {
            switch (shotsInRow[j]) {

                case "*" :
                    opponentGameBoardContainer.find("#field" + i + j).css("background", "cornflowerblue");
                    break;
                case "X" :
                    opponentGameBoardContainer.find("#field" + i + j).css("background", "firebrick");
                    break;
                case "-" :
                    opponentGameBoardContainer.find("#field" + i + j).css("background", "darkolivegreen");
                    break;
                case "." :
                    break;
                default :

            }
        }
    }

}

function setupGame(gameStatus) {
    $("#opponentPlayerName h4").text(gameStatus.full_name);
    gameId = parseInt(gameStatus.game_id);
    $("#checkGameBtn").click();

}

// make the grid columns and rows
function drawGameBoard(size, player) {
    for (i = 0; i < size; i++) {
        for (j = 0; j < size; j++) {

            var square = $("<div>");
            // unique id for each field
            square.attr("id", "field" + j + i);

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

        $(e.target).css("background", "darkorange");

        firedSalvo.salvo.push(row + "x" + col);
    }
    e.stopPropagation();
}

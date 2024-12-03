package model;

import websocket.commands.UserGameCommand;

public record UserGameCommandData(UserGameCommand.CommandType commandType, String authToken, Integer gameID) {
}

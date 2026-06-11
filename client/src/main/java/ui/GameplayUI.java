package ui;

import chess.*;
import client.ResponseException;
import client.ServerFacade;
import model.GameData;
import request.*;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.DrawBoard.drawBoard;
import static ui.EscapeSequences.*;

public class GameplayUI implements ServerMessageHandler {
    private final ServerFacade server;
    private WebSocketFacade ws = null;
    private GameData gamedata;
    private ChessGame chessGame;
    private final String teamColor;
    private final String username;
    private String authToken = null;

    private final String border = SET_BG_COLOR_BLACK;
    private final String text = RESET_TEXT_COLOR;
    private final String lightSquares = SET_BG_COLOR_LIGHT_GREY;
    private final String darkSquares = SET_BG_COLOR_DARK_GREY;
    private final String whitePieces = SET_TEXT_COLOR_WHITE;
    private final String blackPieces = SET_TEXT_COLOR_BLACK;

    private final String inputColor = SET_TEXT_COLOR_BLUE;
    private final String outputColor = RESET_TEXT_COLOR;
    private final String errorColor = SET_TEXT_COLOR_RED;
    private final String messageColor = SET_TEXT_COLOR_YELLOW;
    private final String defaultColor = RESET_TEXT_COLOR;

    public GameplayUI(ServerFacade server, GameData gameData, String teamColor) {
        this.server = server;
        this.gamedata = gameData;
        this.chessGame = gameData.game();
        this.teamColor = teamColor;
        username = switch (teamColor) {
            case "WHITE" -> gameData.whiteUsername();
            case "BLACK" -> gameData.blackUsername();
            default -> null;
        };
        try {
            this.ws = new WebSocketFacade(server.getServerUrl(), this);
            ws.connect(gameData.gameID(), username);
        } catch (ResponseException e) {
            System.out.println(errorColor + "Unable to connect with websocket: " + e + defaultColor);
        }
    }

    String prompt = "[GAMEPLAY] >>> ";
    public String start(String authToken) {
        this.authToken = authToken;
        System.out.println(redraw());
        System.out.println(help());
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("Exiting...") && !result.equals("Leaving...") && (ws != null)) {
            System.out.print(inputColor + prompt + defaultColor);
            String line = scanner.nextLine();
            result = eval(line);
            System.out.println(result);
        }
        return result;
    }

    private String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = (!cmd.equals("help")) ? Arrays.copyOfRange(tokens, 1, tokens.length) : null;
        try {
            return switch (cmd) {
                case "redraw" -> redraw();
                case "move" -> move(params);
                case "leave" -> leave();
                case "quit" -> "Exiting...";
                case "help" -> help();
                default -> {
                    System.out.print("'" + cmd + "' was not a recognized command. ");
                    yield help();
                }
            };
        } catch (ResponseException e) {
            return errorColor + e.getMessage() + "\n" + defaultColor;
        }
    }

    private String redraw() {
        chessGame = gamedata.game();
        ChessGame.TeamColor color;
        if (teamColor == null) {
            color = WHITE;
        }
        else if (teamColor.equals("BLACK")) {
            color = BLACK;
        }
        else {
            color = WHITE;
        }
        drawBoard(chessGame.getBoard(), color, border, text, lightSquares, darkSquares, whitePieces, blackPieces);
        return String.format("%s to move\n", chessGame.getTeamTurn().toString());
    }

    private String redraw(ChessMove move) {
        chessGame = gamedata.game();
        ChessGame.TeamColor color;
        String moveColor = SET_BG_COLOR_BLUE;
        if (teamColor == null) {
            color = WHITE;
        }
        else if (teamColor.equals("BLACK")) {
            color = BLACK;
        }
        else {
            color = WHITE;
        }
        drawBoard(chessGame.getBoard(), color, border, text, lightSquares, darkSquares, whitePieces, blackPieces, moveColor, move);
        return String.format("%s to move\n", chessGame.getTeamTurn().toString());
    }

    private String leave() throws ResponseException {
        ws.leave(gamedata.gameID(), username);
        return "Leaving...";
    }

    private String move(String... params) throws ResponseException {
        if (params.length >= 2) {
            ChessMove tempMove = stringToChessMove(params[0], params[1]);
            ChessMove move = includePromotion(tempMove);
            ws.makeMove(authToken, gamedata.gameID(), move);
        }
        return params[0] + " moved to " + params[1];
    }

    private ChessMove stringToChessMove(String start, String end) throws ResponseException {
        validateStringLength(start);
        validateStringLength(end);
        int startColumn = fileToInt(start.substring(0,1));
        int startRow = rankToInt(start.substring(1));
        int endColumn = fileToInt(end.substring(0,1));
        int endRow = rankToInt(end.substring(1));
        ChessPosition startPos = new ChessPosition(startRow, startColumn);
        ChessPosition endPos = new ChessPosition(endRow, endColumn);
        return new ChessMove(startPos, endPos, null);
    }

    private void validateStringLength(String str) throws ResponseException {
        int size = 2;
        if (str.length() != size) {
            throw new ResponseException("Error: Expected character count of " + str + " was " + size);
        }
    }

    private int fileToInt(String str) throws ResponseException {
        String columns = "abcdefgh";
        if (!columns.contains(str)) {
            throw new ResponseException("Error: " + str + " is not a recognized column");
        }
        return columns.indexOf(str) + 1;
    }

    private int rankToInt(String str) throws ResponseException {
        int row;
        try {
            row = Integer.parseInt(str);
        } catch (Exception e) {
            throw new ResponseException("Error: " + str + " is not a recognized row");
        }
        if (row < 1 || row > 8) {
            throw new ResponseException("Error: " + str + " is not a recognized row");
        }
        return row;
    }

    private ChessMove includePromotion(ChessMove move) {
        ChessBoard board = chessGame.getBoard();
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && piece.isPromotionRank(move.getEndPosition())) {
            ChessPiece.PieceType promotionPiece = promptPromotion();
            return new ChessMove(move.getStartPosition(), move.getEndPosition(), promotionPiece);
        }
        return move;
    }

    private ChessPiece.PieceType promptPromotion() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(inputColor + "Select promotion piece: [Q]ueen, [R]ook, [B]ishop, K[N]ight >>> " + defaultColor);
            String line = scanner.nextLine();
            String[] tokens = line.toLowerCase().split(" ");
            String piece = (tokens.length > 0) ? tokens[0] : "";
            switch (piece) {
                case "q" -> {
                    return ChessPiece.PieceType.QUEEN;
                }
                case "r" -> {
                    return ChessPiece.PieceType.ROOK;
                }
                case "b" -> {
                    return ChessPiece.PieceType.BISHOP;
                }
                case "n" -> {
                    return ChessPiece.PieceType.KNIGHT;
                }
            }
            System.out.println(errorColor + "Error: unrecognized piece" + defaultColor);
        }
    }

    private String help() {
        return outputColor + """
                These are your options:
                  redraw - the board
                  highlight <SQUARE> - a piece's legal moves
                  move <START_SQUARE> <END_SQUARE> - a piece
                  resign - the game
                  leave - the game
                  quit - playing chess
                  help - with possible commands
                """ + defaultColor;
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        System.out.print("\033[2K\r");
        System.out.flush();
        switch (serverMessage.getServerMessageType()) {
            // DEBUG: When someone joins the game for the first time, their username pops up as null
            case NOTIFICATION -> System.out.println(messageColor + serverMessage.getMessage() + defaultColor);
            case ERROR -> System.out.println(errorColor + serverMessage.getMessage() + defaultColor);
            case LOAD_GAME -> {
                System.out.println(messageColor + serverMessage.getMessage() + defaultColor);
                try {
                    gamedata = server.listGames()[gamedata.gameID()-1];
                } catch (ResponseException e) {
                    System.out.println(errorColor + e + defaultColor);
                }
                ChessMove move = serverMessage.getMove();
                if (move != null) {
                    System.out.println(redraw(move));
                }
                else {
                    System.out.println(redraw());
                }
            }
        }
        System.out.print(inputColor + prompt + defaultColor);
    }
}

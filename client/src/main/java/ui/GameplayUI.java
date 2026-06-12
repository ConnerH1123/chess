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
import java.util.HashSet;
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

    public GameplayUI(ServerFacade server, String authToken, GameData gameData, String teamColor) {
        this.server = server;
        this.authToken = authToken;
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
            ws.connect(authToken, gameData.gameID(), username);
        } catch (ResponseException e) {
            System.out.println(errorColor + "Unable to connect with websocket: " + e + defaultColor);
        }
    }

    String prompt = "[GAMEPLAY] >>> ";
    public String start() {
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
                case "highlight" -> highlight(params);
                case "move" -> move(params);
                case "resign" -> resign();
                case "leave" -> leave();
                case "quit" -> {
                    ws.leave(authToken, gamedata.gameID(), username);
                    yield "Exiting...";
                }
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
        ChessGame.TeamColor color = getColor();
        drawBoard(chessGame.getBoard(), color, border, text, lightSquares, darkSquares, whitePieces, blackPieces);
        return chessGame.gameStatusToString() + "\n";
    }

    private String redraw(ChessMove move) {
        chessGame = gamedata.game();
        ChessGame.TeamColor color = getColor();
        String moveColor = SET_BG_COLOR_BLUE;
        drawBoard(chessGame.getBoard(), color, border, text, lightSquares, darkSquares, whitePieces, blackPieces, moveColor, move);
        return "";
    }

    private ChessGame.TeamColor getColor() {
        if (teamColor == null) {
            return WHITE;
        }
        else if (teamColor.equals("BLACK")) {
            return BLACK;
        }
        else {
            return WHITE;
        }

    }

    private String highlight(String... params) throws ResponseException {
        if (params.length < 1) {
            throw new ResponseException("Error: Insufficient arguments. Expected <SQUARE>");
        }
        chessGame = gamedata.game();
        ChessGame.TeamColor color = getColor();
        ChessPosition position = stringToPosition(params[0]);
        ChessBoard board = chessGame.getBoard();
        HashSet<ChessMove> moves = (HashSet<ChessMove>) chessGame.validMoves(position);
        String startColor = SET_BG_COLOR_RED;
        String moveColor = SET_BG_COLOR_YELLOW;
        String captureColor = SET_TEXT_COLOR_RED;
        drawBoard(board, color, border, text, lightSquares, darkSquares, whitePieces, blackPieces, moveColor, startColor, captureColor, moves);
        return chessGame.gameStatusToString() + "\n";
    }

    private ChessPosition stringToPosition(String str) throws ResponseException {
        validateStringLength(str);
        int col = fileToInt(str.substring(0,1));
        int row = rankToInt(str.substring(1));
        return new ChessPosition(row, col);
    }


    private String leave() throws ResponseException {
        ws.leave(authToken, gamedata.gameID(), username);
        return "Leaving...";
    }

    private String move(String... params) throws ResponseException {
        if (params.length >= 2) {
            ChessMove tempMove = stringToChessMove(params[0], params[1]);
            ChessMove move = includePromotion(tempMove);
            ws.makeMove(authToken, gamedata.gameID(), username, move);
            return "Moving piece...";
        }
        else {
            throw new ResponseException("Error: Insufficient arguments. Expected <START_SQUARE> <END_SQUARE>");
        }
    }

    private ChessMove stringToChessMove(String start, String end) throws ResponseException {
        ChessPosition startPos = stringToPosition(start);
        ChessPosition endPos = stringToPosition(end);
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

    private String resign() throws ResponseException {
        ws.resign(authToken, gamedata.gameID(), username);
        return "Resigning...";
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
            case NOTIFICATION -> System.out.println(messageColor + serverMessage.getMessage() + defaultColor);
            case ERROR -> System.out.println(errorColor + serverMessage.getErrorMessage() + defaultColor);
            case LOAD_GAME -> {
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

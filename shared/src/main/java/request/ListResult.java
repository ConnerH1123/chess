package request;

import model.GameData;

public record ListResult(GameData[] games) {
}

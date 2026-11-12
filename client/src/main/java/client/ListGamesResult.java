package client;

import java.util.Collection;

public record ListGamesResult(Collection<GameListEntry> games) {
}

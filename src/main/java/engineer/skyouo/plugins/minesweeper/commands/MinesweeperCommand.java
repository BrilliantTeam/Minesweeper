package engineer.skyouo.plugins.minesweeper.commands;

import engineer.skyouo.plugins.minesweeper.HeadUtil;
import engineer.skyouo.plugins.minesweeper.games.MinesweeperGame;
import engineer.skyouo.plugins.minesweeper.games.Point;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MinesweeperCommand implements TabExecutor {
    private static Map<UUID, MinesweeperGame> games = new HashMap<>();
    private static Map<UUID, Boolean> flagging = new HashMap<>(); // 你是懂正規化的
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;

        switch (args[0]) {
            case "start": {
                if (games.containsKey(player.getUniqueId())) {
                    sender.sendMessage(
                            Component.text(
                                    "您目前已經有正在進行的掃雷遊戲!"
                            ).color(TextColor.color(255, 0, 0))
                    );
                    return true;
                }

                if (args.length < 3) {
                    sender.sendMessage(
                            Component.text(
                                    "用法: /minesweeper start <大小> <地雷數量>\n或 /minesweeper start <x 大小> <y 大小> <地雷數量>"
                            ).color(TextColor.color(255, 0, 0))
                    );
                    return true;
                }

                if (!tryParseInt(args[1]) || !tryParseInt(args[2])) {
                    sender.sendMessage(
                            Component.text(
                                    "用法: /minesweeper start <大小> <地雷數量>\n或 /minesweeper start <x 大小> <y 大小> <地雷數量>"
                            ).color(TextColor.color(255, 0, 0))
                    );
                    return true;
                }

                if (args.length == 3) {
                    int size = Integer.parseInt(args[1]);
                    int landmineCount = Integer.parseInt(args[2]);

                    if (size < 3) {
                        sender.sendMessage(
                                Component.text(
                                        "大小不得小於三!"
                                ).color(TextColor.color(255, 0, 0))
                        );
                        return true;
                    }

                    if (size * size > 82) {
                        sender.sendMessage(
                                Component.text(
                                        "請嘗試將大小設置為小一點的數值, 否則伺服器端將會無法發送圖形化消息!"
                                ).color(TextColor.color(255, 0, 0))
                        );
                        return true;
                    }

                    if (landmineCount > size * size) {
                        sender.sendMessage(
                                Component.text(
                                        "地雷數量不得大於大小的平方!"
                                ).color(TextColor.color(255, 0, 0))
                        );
                        return true;
                    }

                    MinesweeperGame game = new MinesweeperGame(size, landmineCount);

                    games.put(player.getUniqueId(), game);

                    TextComponent textComponent = serializeGame(player, game);

                    player.sendMessage(textComponent);
                } else {
                    if (!tryParseInt(args[3])) {
                        sender.sendMessage(
                                Component.text(
                                        "用法: /minesweeper start <大小> <地雷數量>\n或 /minesweeper start <x 大小> <y 大小> <地雷數量>"
                                ).color(TextColor.color(255, 0, 0))
                        );
                        return true;
                    }

                    int sizeX = Integer.parseInt(args[1]);
                    int sizeY = Integer.parseInt(args[2]);
                    int landmineCount = Integer.parseInt(args[3]);

                    if (sizeX < 3 || sizeY < 3) {
                        sender.sendMessage(
                                Component.text(
                                        "大小不得小於三!"
                                ).color(TextColor.color(255, 0, 0))
                        );
                        return true;
                    }

                    if (sizeX * sizeY > 82) {
                        sender.sendMessage(
                                Component.text(
                                        "請嘗試將大小設置為小一點的數值, 否則伺服器端將會無法發送圖形化消息!"
                                ).color(TextColor.color(255, 0, 0))
                        );
                        return true;
                    }

                    if (landmineCount > sizeX * sizeY) {
                        sender.sendMessage(
                                Component.text(
                                        "地雷數量不得大於大小!"
                                ).color(TextColor.color(255, 0, 0))
                        );
                        return true;
                    }

                    MinesweeperGame game = new MinesweeperGame(sizeX, sizeY, landmineCount);

                    games.put(player.getUniqueId(), game);

                    TextComponent textComponent = serializeGame(player, game);

                    player.sendMessage(textComponent);
                }
                break;
            }
            case "sweep": {
                if (!games.containsKey(player.getUniqueId())) {
                    return true;
                }

                if (args.length < 3) {
                    sender.sendMessage(
                            Component.text(
                                    "用法: /minesweeper sweep <x> <y>"
                            ).color(TextColor.color(255, 0, 0))
                    );
                    return true;
                }

                if (!tryParseInt(args[1]) || !tryParseInt(args[2])) {
                    sender.sendMessage(
                            Component.text(
                                    "用法: /minesweeper sweep <x> <y>"
                            ).color(TextColor.color(255, 0, 0))
                    );
                    return true;
                }

                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);

                MinesweeperGame game = games.get(player.getUniqueId());
                boolean result = game.sweep(x, y);

                if (!result)
                    return true;

                TextComponent textComponent = serializeGame(player, game);

                player.sendMessage(textComponent);

                if (game.gameOver) {
                    player.sendMessage(Component.text("遊戲結束!").color(TextColor.color(255, 255, 66)));

                    games.remove(player.getUniqueId());
                    flagging.remove(player.getUniqueId());
                }
                break;
            }
            case "flag": {
                if (!games.containsKey(player.getUniqueId())) {
                    return true;
                }

                if (args.length < 3) {
                    sender.sendMessage(
                            Component.text(
                                    "用法: /minesweeper flag <x> <y>"
                            ).color(TextColor.color(255, 0, 0))
                    );
                    return true;
                }

                if (!tryParseInt(args[1]) || !tryParseInt(args[2])) {
                    sender.sendMessage(
                            Component.text(
                                    "用法: /minesweeper flag <x> <y>"
                            ).color(TextColor.color(255, 0, 0))
                    );
                    return true;
                }

                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);

                MinesweeperGame game = games.get(player.getUniqueId());
                boolean result = game.flag(x, y);

                if (!result)
                    return true;

                TextComponent textComponent = serializeGame(player, game);

                player.sendMessage(textComponent);

                if (game.gameOver) {
                    player.sendMessage(Component.text("遊戲結束!").color(TextColor.color(255, 255, 66)));

                    games.remove(player.getUniqueId());
                    flagging.remove(player.getUniqueId());
                }
                break;
            }
            case "switch": {
                if (!games.containsKey(player.getUniqueId())) {
                    return true;
                }

                flagging.put(player.getUniqueId(),
                        !flagging.getOrDefault(player.getUniqueId(), false)
                );

                MinesweeperGame game = games.get(player.getUniqueId());

                TextComponent textComponent = serializeGame(player, game);

                player.sendMessage(textComponent);

                if (game.gameOver) {
                    player.sendMessage(Component.text("遊戲結束!").color(TextColor.color(255, 255, 66)));

                    games.remove(player.getUniqueId());
                    flagging.remove(player.getUniqueId());
                }
                break;
            }
            case "stop": {
                if (!games.containsKey(player.getUniqueId())) {
                    return true;
                }

                games.remove(player.getUniqueId());
                flagging.remove(player.getUniqueId());
                break;
            }
            default:
        }

        return true;
    }

    private boolean tryParseInt(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public TextComponent serializeGame(Player player, MinesweeperGame game) {
        TextComponent component = Component.text("");
        for (int y = 0; y < game.sizeY; y++) {
            for (int x = 0; x < game.sizeX; x++) {
                TextColor[][] colors = decideTextColor(game.landmines[y][x]);

                component = component.append(addHeadComponent(x, y, colors).clickEvent(
                        !game.landmines[y][x].isVisable() ?
                                (flagging.getOrDefault(player.getUniqueId(), false) ?
                                ClickEvent.runCommand("/minesweeper flag " + x + " " + y) : ClickEvent.runCommand("/minesweeper sweep " + x + " " + y)) :
                                null
                )).append(Component.text("\uF008 \uE003"));
            }

            component = component.append(Component.text("\n"));
        }

        component = component
                .append(Component.text("\n"))
                .append(Component.text(
                        flagging.getOrDefault(player.getUniqueId(), false) ?
                                "[切換到探雷模式]" : "[切換到標記模式]")
                        .color(
                                flagging.getOrDefault(player.getUniqueId(), false) ?
                                    TextColor.color(0, 255, 0) : TextColor.color(255, 0, 0)
                        ).clickEvent(ClickEvent.runCommand("/minesweeper switch"))
                );

        return component;
    }

    private static TextComponent addHeadComponent(int x, int y, TextColor[][] colors) {
        TextComponent component = Component.text("");
        for (int i = 0; i < colors.length; i++) {
            for (int j = 0; j < colors.length; j++) {
                component = component.append(
                        Component.text((char) (((int) '\uF810') + i)).color(colors[i][j])
                );

                component = component.append(
                        Component.text('\uE001')
                );
            }

            component = component.append(
                    Component.text('\uE008')
            );
        }

        return component;
    }

    private TextColor[][] decideTextColor(Point point) {
        if (point.isFlagged())
            return HeadUtil.FLAGGED;

        if (!point.isVisable())
            return HeadUtil.UNKNOWN;

        return switch (point.getValue()) {
            case 0 -> HeadUtil.ZERO;
            case 1 -> HeadUtil.ONE;
            case 2 -> HeadUtil.TWO;
            case 3 -> HeadUtil.THREE;
            case 4 -> HeadUtil.FOUR;
            case 5 -> HeadUtil.FIVE;
            case 6 -> HeadUtil.SIX;
            case 7 -> HeadUtil.SEVEN;
            case 8 -> HeadUtil.EIGHT;
            case -1 -> HeadUtil.TNT;
            default -> HeadUtil.UNKNOWN;
        };
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0)
            return List.of("start", "sweep", "flag", "stop");
        else if (args.length == 1)
            return Stream.of("start", "sweep", "flag", "switch", "stop").filter(item -> item.startsWith(args[0])).collect(Collectors.toList());
        else
            return List.of();
    }
}

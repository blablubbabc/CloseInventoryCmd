# A Bukkit / Spigot plugin that adds a command for closing a player's inventory

Works on Bukkit 1.14 and above.

## Usage: `/closeinventory [player]`
Aliases: `closeinv`, `close`

## Default config:

See https://github.com/blablubbabc/CloseInventoryCmd/blob/master/src/main/resources/config.yml

## Permissions:

* `closeinventory.own`: Allows closing the own inventory with the closeinventory command. (default: true)
* `closeinventory.others`: Allows closing the inventory of other players with the closeinventory command. (default: op)

Players are required to have one of these permissions to see the command. Otherwise they receive Bukkit's generic 'no permission' message when they attempt to use the command.

## License:

Feel free to do whatever you want with this code.

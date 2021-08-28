package xyz.refinedev.practice.profile.statistics.menu;

/*@AllArgsConstructor
public class StatsMenu extends Menu {

    private static final BasicConfigurationFile config = Array.getInstance().getMenuConfig();

    private final Player target;

    @Override
    public String getTitle(Player player) {
        return "&7" + target.getName() + "'s Statistics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new GlobalStatsButton());
        for ( Kit kit : Kit.getKits()) {
            if (kit.isEnabled()) {
                buttons.put(buttons.size(), new KitStatsButton(kit));
            }
        }

        return buttons;
    }

    @AllArgsConstructor
    private class KitStatsButton extends Button {

        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(target.getUniqueId());
            String elo = kit.getGameRules().isRanked() ? Integer.toString(profile.getStatisticsData().get(kit).getElo()) : "N/A";

            config.getStringList("MENUS.STATISTICS.KIT_LORE").forEach(line -> {
                lore.add(CC.translate(line
                        .replace("<profile_kit_elo>", elo)
                        .replace("<profile_kit_wins>", String.valueOf(profile.getStatisticsData().get(kit).getWon()))
                        .replace("<profile_kit_losses>", String.valueOf(profile.getStatisticsData().get(kit).getLost()))));
            });

            String name = CC.translate(config.getString("MENUS.STATISTICS.KIT_NAME").replace("<kit>", kit.getDisplayName()));

            return new ItemBuilder(kit.getDisplayIcon())
                    .name(name)
                    .lore(lore)
                    .build();
        }

    }

    private class GlobalStatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(target.getUniqueId());

            config.getStringList("MENUS.STATISTICS.GLOBAL_LORE").forEach(line -> {
                lore.add(line
                        .replace("<profile_global_elo>", String.valueOf(profile.getGlobalElo()))
                        .replace("<profile_global_wins>", String.valueOf(profile.getTotalWins()))
                        .replace("<profile_global_losses>", String.valueOf(profile.getTotalLost()))
                        .replace("<profile_elo_division>", profile.getDivision())
                        .replace("<profile_wr_ratio>", String.valueOf(profile.getWLR())));

            });

            return new ItemBuilder(SkullCreator.itemFromUuid(target.getUniqueId()))
                    .name(CC.translate(config.getString("MENUS.STATISTICS.GLOBAL_NAME")))
                    .lore(lore)
                    .build();
        }

    }

}*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import processing.core.PImage;

public class Mushroom extends Actionable {

    public static final String MUSHROOM_KEY = "mushroom";

    public Mushroom(String id, Point position, List<PImage> images, double behaviorPeriod) {
        super(id, position, images, 0, 0, behaviorPeriod);
    }

    @Override
    public void updateImage() {
        throw new UnsupportedOperationException("Unimplemented method 'updateImage'");
    }

    public void executeActivity(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        List<Point> adjacentPositions = new ArrayList<>(List.of(
                new Point(this.getPosition().x - 1, this.getPosition().y),
                new Point(this.getPosition().x + 1, this.getPosition().y),
                new Point(this.getPosition().x, this.getPosition().y - 1),
                new Point(this.getPosition().x, this.getPosition().y + 1)));
        Collections.shuffle(adjacentPositions);

        List<Point> mushroomBackgroundPositions = new ArrayList<>();
        List<Point> mushroomEntityPositions = new ArrayList<>();
        for (Point adjacentPosition : adjacentPositions) {
            if (world.inBounds(adjacentPosition) && !world.isOccupied(adjacentPosition)
                    && world.hasBackground(adjacentPosition)) {
                Background bg = world.getBackgroundCell(adjacentPosition);
                if (bg.getId().equals("grass")) {
                    mushroomBackgroundPositions.add(adjacentPosition);
                } else if (bg.getId().equals("grass_mushrooms")) {
                    mushroomEntityPositions.add(adjacentPosition);
                }
            }
        }

        if (!mushroomBackgroundPositions.isEmpty()) {
            Point position = mushroomBackgroundPositions.get(0);

            Background background = new Background("grass_mushrooms", imageLibrary.get("grass_mushrooms"), 0);
            world.setBackgroundCell(position, background);
        } else if (!mushroomEntityPositions.isEmpty()) {
            Point position = mushroomEntityPositions.get(0);

            List<Point> adjacentPoints = new ArrayList<>(List.of(
                    new Point(position.x - 1, position.y),
                    new Point(position.x + 1, position.y),
                    new Point(position.x, position.y - 1),
                    new Point(position.x, position.y + 1)));

            Mushroom mushroom = new Mushroom(MUSHROOM_KEY, position, imageLibrary.get(MUSHROOM_KEY),
                    this.getBehaviorPeriod() * 4.0);

            if (Math.random() > 0.8) {
                for (Point adjacentPosition : adjacentPoints) {
                    if (world.inBounds(adjacentPosition) && !world.isOccupied(adjacentPosition)
                            && world.hasBackground(adjacentPosition)) {
                        Zombie zomb = new Zombie(Zombie.ZOMBIE_KEY, adjacentPosition,
                                imageLibrary.get(Zombie.ZOMBIE_KEY),
                                NumberUtil.getRandomDouble(Dude.DUDE_ANNIMATION_MIN,
                                        Dude.DUDE_ANNIMATION_MAX) / 2,
                                NumberUtil.getRandomDouble(Dude.DUDE_BEHAVIOR_MIN,
                                        Dude.DUDE_BEHAVIOR_MAX) / 2);
                        System.out.println("zombie spawned");
                        world.addEntity(zomb);
                        zomb.scheduleActions(scheduler, world, imageLibrary);
                        break;
                    }
                }

            }

            world.addEntity(mushroom);
            mushroom.scheduleActions(scheduler, world, imageLibrary);
        }

        scheduleBehavior(scheduler, world, imageLibrary);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, new Behavior((Entity) this, world, imageLibrary), this.getBehaviorPeriod());
    }
}

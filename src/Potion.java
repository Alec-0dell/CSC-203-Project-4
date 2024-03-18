
import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public class Potion extends Actionable {

    public static final String POTION_KEY = "potion";

    public static final int POTION_STAGE_LIMIT = 5;
    public static final double POTION_BEHAVIOR_PERIOD = 0.1;
    public static final double POTION_ANIMATION_PERIOD = 0.01;

    private int stage;
    private Optional<Entity> entity;

    public Potion(String id, Point position, List<PImage> images, Optional<Entity> entity) {
        super(id, position, images, 0, POTION_ANIMATION_PERIOD, POTION_BEHAVIOR_PERIOD);
        this.stage = 1;
        this.entity = entity;
    }

    public boolean transformTarget(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
        // need to remove targeted object adn replace it with potion then replace potion
        // with the wanted entity
        if (this.stage >= SAPLING_HEALTH_LIMIT) {
            // Transform what every entity is pressed.
            if (!entity.isPresent()) {
                Tree tree = new Tree(
                        Tree.TREE_KEY + "_" + this.getId(),
                        this.getPosition(),
                        imageLibrary.get(Tree.TREE_KEY),
                        NumberUtil.getRandomDouble(Tree.TREE_RANDOM_ANIMATION_PERIOD_MIN,
                                Tree.TREE_RANDOM_ANIMATION_PERIOD_MAX),
                        NumberUtil.getRandomDouble(Tree.TREE_RANDOM_BEHAVIOR_PERIOD_MIN,
                                Tree.TREE_RANDOM_BEHAVIOR_PERIOD_MAX),
                        NumberUtil.getRandomInt(Tree.TREE_RANDOM_HEALTH_MIN, Tree.TREE_RANDOM_HEALTH_MAX));
                world.removeEntity(scheduler, this);
                world.addEntity(tree);
                tree.scheduleActions(scheduler, world, imageLibrary);
            } else {
                Entity ent = entity.get();
                if (ent.getClass() == Tree.class || ent.getClass() == Mushroom.class) {
                    world.removeEntity(scheduler, this);
                } else if (ent.getClass() == Fairy.class) {
                    Fairy far = new Fairy(ent.getId(), this.getPosition(), ent.getImages(),
                            ((Actionable) ent).getAnimationPeriod(), ((Actionable) ent).getBehaviorPeriod());
                    world.removeEntity(scheduler, this);
                    world.addEntity(far);
                    far.scheduleActions(scheduler, world, imageLibrary);
                    far.superfy();
                } else if (ent.getClass() == Dude.class) {
                    Dude dud = new Dude(ent.getId(), this.getPosition(), ent.getImages(),
                            ((Actionable) ent).getAnimationPeriod(), ((Actionable) ent).getBehaviorPeriod(),
                            ((Resourceable) ent).getResourceCount(), ((Resourceable) ent).getResourceLimit());
                    world.removeEntity(scheduler, this);
                    world.addEntity(dud);
                    dud.scheduleActions(scheduler, world, imageLibrary);
                    dud.superfy();
                } else if (ent.getClass() == Zombie.class) {
                    Point pos = this.getPosition();
                    world.removeEntity(scheduler, this);
                    Dude dude = new Dude(
                            Dude.DUDE_KEY + "_" + this.getId(),
                            pos,
                            imageLibrary.get(Dude.DUDE_KEY),
                            NumberUtil.getRandomDouble(Dude.DUDE_ANNIMATION_MIN,
                                    Dude.DUDE_ANNIMATION_MAX),
                            NumberUtil.getRandomDouble(Dude.DUDE_BEHAVIOR_MIN,
                                    Dude.DUDE_BEHAVIOR_MAX),
                            0, NumberUtil.getRandomInt(1, 6));
                    world.addEntity(dude);
                    dude.scheduleActions(scheduler, world, imageLibrary);
                } else if (ent.getClass() == Potion.class) {
                    world.removeEntity(scheduler, ent);
                    world.removeEntity(scheduler, this);
                } else {
                    world.removeEntity(scheduler, this);
                }
            }
            return true;
        }
        return false;
    }

    public void executeActivity(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        this.stage = this.stage + 1;
        if (!transformTarget(world, scheduler, imageLibrary)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    @Override
    public void updateImage() {
        this.setImageIndex(stage - 1);
    }

}

public class Behavior extends Action{

    private final World world;
    private final ImageLibrary imageLibrary;

    public Behavior(Entity entity, World world, ImageLibrary imageLibrary){
        super(entity);
        this.world = world;
        this.imageLibrary = imageLibrary;
    }
    
    public void execute(EventScheduler scheduler) {
        ((Actionable)super.getEntity()).executeActivity(world, imageLibrary, scheduler);
    }

}

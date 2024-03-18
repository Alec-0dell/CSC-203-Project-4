import java.util.List;

public class PathingNode extends Point implements Comparable<PathingNode>{
    private List<Point> path; //g-score = path.length
    private Point end;
    private int hScore;
    private int fScore;

    public PathingNode(Point point, List<Point> path, Point start, Point end){
        super(point.x, point.y);
        this.end = end;
        this.path = path;
        this.calculateScores();
    }

    public void calculateScores(){
        this.calcFScore();
        this.calcHScore();
    }


    public void calcHScore(){
        this.hScore = super.manhattanDistanceTo(this.end);
    }

    public void calcFScore(){
        this.fScore = this.getGScore() + super.manhattanDistanceTo(this.end);
    }

    public void setPath(List<Point> path) {
        this.path = path;
        calculateScores();
    }

    public int getGScore() {
        if (this.path == null){
            return 0;
        }
        return this.path.size();
    }

    public int getHScore() {
        return hScore;
    }

    public int getFScore() {
        return fScore;
    }

    public List<Point> getPath() {
        return path;
    }


    @Override
    public int compareTo(PathingNode o) {
        if (o instanceof PathingNode) {
            if (this.equals(o)){
                return 0;
            }
            return 1;
        }
        return 1;
    }
}

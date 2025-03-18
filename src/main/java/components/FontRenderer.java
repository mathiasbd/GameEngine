package components;

public class FontRenderer extends Component {

    private boolean firstTime = true;
    @Override
    public void start() {
        System.out.println("Starting font renderer");
    }

    @Override
    public void update(float dt) {
        if(firstTime) {
            System.out.println("Updating font renderer");
            firstTime = false;
        }
    }

}

package components;

public class SpriteRenderer extends Component {

    private boolean firstTime = true;

    // this method is used to start the sprite
    @Override
    public void start() {
        System.out.println("Starting sprite component");
    }
    // this method is used to update the sprite
    @Override
    public void update(float dt) {
        if(firstTime) {
            System.out.println("updating sprite renderer");
            firstTime = false;
        }
    }
}

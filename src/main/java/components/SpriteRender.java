package components;

import org.example.Component;

import javax.swing.*;

public class SpriteRender extends Component {

    private boolean firstTime = false;

    // this method is used to start the sprite
    @Override
    public void start() {
        System.out.println("Starting");
    }
    // this method is used to update the sprite
    @Override
    public void update(float dt) {
        System.out.println("updating");
        firstTime = true;
    }
}

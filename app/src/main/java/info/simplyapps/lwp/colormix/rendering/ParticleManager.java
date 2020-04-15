package info.simplyapps.lwp.colormix.rendering;

public class ParticleManager {

    ValueContainer valueContainer;
    public float nTimeCounter = 0;
    public float nRnd = 0;
    ParticleUpdateThread pThread;

    public ParticleManager(ValueContainer valueContainer) {
        this.valueContainer = valueContainer;
        pThread = new ParticleUpdateThread(this);
    }

    public void start() {
        pThread.setRunning(true);
        pThread.start();
    }

    public void stop() {
        boolean retry = true;
        pThread.setRunning(false);
        while (retry) {
            try {
                pThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public void update() {
        nTimeCounter += 0.01;
        if (nTimeCounter >= Math.PI * 2)
            nTimeCounter = 0;

        nRnd = RenderUtil.rnd(0.01f, 0.99f);

        valueContainer.rnd = nRnd;
        valueContainer.time = nTimeCounter;
    }


}

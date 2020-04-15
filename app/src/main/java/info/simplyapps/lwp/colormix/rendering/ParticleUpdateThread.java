package info.simplyapps.lwp.colormix.rendering;

public class ParticleUpdateThread extends Thread {

    ParticleManager pManager;

    private final static int MAX_FPS = 30;
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;
    Boolean bRunning = false;

    public ParticleUpdateThread(ParticleManager pm) {
        setName("particleManager");
        this.pManager = pm;
    }

    public void setRunning(Boolean brun) {
        bRunning = brun;
    }

    public Boolean getRuning() {
        return bRunning;
    }

    public void run() {
        long lBeginTime;
        long lElapsedTime;
        int iSleepTime;
        while (bRunning) {
            lBeginTime = System.currentTimeMillis();
            if (pManager != null) {
                synchronized (pManager) {
                    pManager.update();
                }
            }
            lElapsedTime = System.currentTimeMillis() - lBeginTime;
            iSleepTime = (int) (FRAME_PERIOD - lElapsedTime);
            if (iSleepTime > 0) {
                try {
                    Thread.sleep(iSleepTime);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
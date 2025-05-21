package jp.ac.gifu_u.info.genki.myapplication;

import android.net.Uri;
import android.opengl.EGLConfig;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    /* --- メンバ変数 --- */
    private VideoView videoView;
    private GLSurfaceView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* 1) GLSurfaceView を用意 */
        glView = new GLSurfaceView(this);
        glView.setRenderer(new MyRenderer());

        /* 2) VideoView を用意 */
        videoView = new VideoView(this);
        videoView.setMediaController(new MediaController(this));
        // res/raw/sample.mp4 などを置いておく
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample);
        videoView.setVideoURI(uri);

        /* 3) 2 つの View を重ねる */
        FrameLayout root = new FrameLayout(this);
        root.addView(videoView,           // 下層に Video
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
        root.addView(glView);             // 上層に OpenGL

        setContentView(root);
    }

    @Override protected void onResume() {
        super.onResume();
        glView.onResume();
        videoView.start();
    }

    @Override protected void onPause() {
        super.onPause();
        glView.onPause();
        videoView.pause();
    }

    /* ----------------- OpenGL Renderer ----------------- */
    private static class MyRenderer implements GLSurfaceView.Renderer {
        private int width, height;
        private FloatBuffer triangle;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig cfg) {
            // 頂点データ用意 (一度だけ)
            triangle = makeBuffer(new float[]{
                    -0.5f, 0f, 0f,
                    0.5f, 0f, 0f,
                    0f,   1f, 0f});
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int w, int h) {
            width = w; height = h;
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            /* ビューポートと射影行列 */
            gl.glViewport(0, 0, width, height);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glOrthof(-1f, 1f, -1f, 1f, 2f, -2f);

            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            gl.glColor4f(1f, 1f, 1f, 1f);

            /* 頂点配列を有効化 → ポインタ設定 → 描画 */
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangle);
            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }

        private static FloatBuffer makeBuffer(float[] v) {
            ByteBuffer bb = ByteBuffer.allocateDirect(v.length * 4).order(ByteOrder.nativeOrder());
            return bb.asFloatBuffer().put(v).position(0), bb.asFloatBuffer();
        }
    }
}

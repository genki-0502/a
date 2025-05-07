package jp.ac.gifu_u.info.genki.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MyView extends View {

    // ─── 軌跡データ ──────────────────────────────
    private final List<Integer> pointsX = new ArrayList<>();
    private final List<Integer> pointsY = new ArrayList<>();
    private final List<Boolean> drawFlags = new ArrayList<>();

    // ─── 描画関連オブジェクト ────────────────────
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint backPaint = new Paint();
    private final Rect  backRect  = new Rect();

    // ─── 色パレット（ロングプレスで切替） ────────
    private final int[] palette = {
            Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.BLACK
    };
    private int colorIndex = 0;

    // ─── ジェスチャ検出 ─────────────────────────
    private final GestureDetector detector;

    // ─── コンストラクタ ─────────────────────────
    public MyView(Context context) {
        super(context);

        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5);
        linePaint.setColor(palette[colorIndex]);

        backPaint.setStyle(Paint.Style.FILL);
        backPaint.setColor(Color.WHITE);

        detector = new GestureDetector(context, new GestureListener());
    }

    // ─── タッチイベント処理 ─────────────────────
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // 追加ジェスチャ（ダブルタップ／ロングプレス）を判定
        detector.onTouchEvent(event);

        int x = Math.round(event.getX());
        int y = Math.round(event.getY());

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                pointsX.add(x);
                pointsY.add(y);
                drawFlags.add(false);      // 押した瞬間は線を引かない
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                pointsX.add(x);
                pointsY.add(y);
                drawFlags.add(true);       // 移動区間を描画
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                pointsX.add(x);
                pointsY.add(y);
                drawFlags.add(true);       // 離す直前まで線を描画
                invalidate();
                break;
        }
        return true;
    }

    // ─── 描画処理 ────────────────────────────────
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 背景を塗りつぶし
        backRect.set(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.drawRect(backRect, backPaint);

        // 軌跡を描画
        for (int i = 1; i < drawFlags.size(); i++) {
            if (drawFlags.get(i)) {
                int x1 = pointsX.get(i - 1);
                int y1 = pointsY.get(i - 1);
                int x2 = pointsX.get(i);
                int y2 = pointsY.get(i);
                canvas.drawLine(x1, y1, x2, y2, linePaint);
            }
        }
    }

    // ─── 追加ジェスチャ用リスナ ──────────────────
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        /** ダブルタップでキャンバスクリア */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            pointsX.clear();
            pointsY.clear();
            drawFlags.clear();
            invalidate();
            return true;
        }

        /** ロングプレスで線の色を切替 */
        @Override
        public void onLongPress(MotionEvent e) {
            colorIndex = (colorIndex + 1) % palette.length;
            linePaint.setColor(palette[colorIndex]);
            invalidate();               // 以降の線が新色になる
        }

        /** onDown を true にすると SimpleOnGestureListener が
         *   MotionEvent を消費してしまうので false のままにする */
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }
    }
}

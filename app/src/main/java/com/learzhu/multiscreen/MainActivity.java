package com.learzhu.multiscreen;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.ActivityTestCase;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button multiScreenBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        multiScreenBtn = (Button) findViewById(R.id.start_multi_screen);
    }

//            Activity类中添加了以下新方法，以支持多窗口显示。
//            Activity.inMultiWindow() 调用该方法以确认 Activity 是否处于多窗口模式。
//            Activity.inPictureInPicture() 调用该方法以确认 Activity 是否处于画中画模式。注：画中画模式是多窗口模式的特例。 如果 myActivity.inPictureInPicture()返回 true，则 myActivity.inMultiWindow()也返回 true。
//            Activity.onMultiWindowChanged() Activity 进入或退出多窗口模式时系统将调用此方法。 在 Activity 进入多窗口模式时，系统向该方法传递 true 值，在退出多窗口模式时，则传递 false 值。
//            Activity.onPictureInPictureChanged() Activity 进入或退出画中画模式时系统将调用此方法。 在 Activity 进入画中画模式时，系统向该方法传递 true 值，在退出画中画模式时，则传递 false 值。
//            每个方法还有 Fragment版本，例如 Fragment.inMultiWindow()。

    /**
     * 在启动新 Activity 时，用户可以提示系统如果可能，应将新 Activity 显示在当前 Activity 旁边。 要执行此操作，可使用标志Intent.FLAG_ACTIVITY_LAUNCH_TO_ADJACENT。传递此标志将请求以下行为：
     * 如果设备处于分屏模式，系统会尝试在启动系统的 Activity 旁创建新 Activity，这样两个 Activity 将共享屏幕。
     * ！！！！！系统并不一定能实现此操作，但如果可以，系统将使两个 Activity 处于相邻的位置。
     * 如果设备不处于分屏模式，则该标志无效。
     * public void click(View v){
     * Intent intent=new Intent();
     * intent.setAction(Intent.ACTION_VIEW);
     * intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_TO_ADJACENT);
     * intent.setData(Uri.parse("http://www.baidu.com"));
     * startActivity(intent);
     * }
     *
     * @param view
     */
    public void multiscreen(View view) {
        Intent startMultiScreenIntent = new Intent();
        startMultiScreenIntent.setAction(Intent.ACTION_VIEW);
//        分屏模式下同应用的两个不同Activity可支持相邻显示
//        说明:N版本API Intent类新增Flag:FLAG_ACTIVITY_LAUNCH_ADJACENT,该Flag仅用于多窗口下的分屏模式(split-screen)。
//        分屏模式下,若上窗口Activity1启动Activity2时设置FLAG_ACTIVITY_LAUNCH_ADJACENT,则Activity2在下窗口下显示,同时上窗口依然显示Activity1,
//        即两者相邻显示。使用时FLAG_ACTIVITY_LAUNCH_ADJACENT要结合FLAG_ACTIVITY_NEW_TASK使用,以确保新Activity运行在单独的Task中。
        startMultiScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMultiScreenIntent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
        startMultiScreenIntent.setData(Uri.parse("http://www.baidu.com"));
        startActivity(startMultiScreenIntent);
    }

    /**
     * 支持拖放
     用户可以在两个 Activity 共享屏幕的同时在这两个 Activity 之间拖放。因此，如果您的应用目前不支持拖放功能，您可以在其中添加此功能。
     android.view.DropPermissions令牌对象，负责指定对接收拖放数据的应用授予的权限。
     View.startDragAndDrop() View.startDrag()的新别名。要启用跨 Activity 拖放，请传递新标志 View.DRAG_FLAG_GLOBAL。如需对接收拖放数据的 Activity 授予 URI 权限，可根据情况传递新标志 View.DRAG_FLAG_GLOBAL_URI_READ或 View.DRAG_FLAG_GLOBAL_URI_WRITE。
     View.cancelDragAndDrop() 取消当前正在进行的拖动操作。只能由发起拖动操作的应用调用。
     View.updateDragShadow() 替换当前正在进行的拖动操作的拖动阴影。只能由发起拖动操作的应用调用。
     Activity.requestDropPermissions() 请求使用 DragEvent中包含的 ClipData传递的内容 URI 的权限。
     */

    /**
     * 此处要特别注意
     * 1.topMost的Activity为分屏时候你所能操作正在交互的Activity OnResume状态
     * 2.onPause但是可见的分屏的Activity的优先级会比其他的onPause的优先级高
     * <p/>
     * 3.2 多窗口下Activity生命周期注意事项
     * 说明:N版本多窗口下运行的两个同时可见的Activity,只有一个处于Resumed状态,有别于华为多窗口的方案(两个可见的Activity同时处于Resumed状态)。
     * 如果是视频、社交、浏览器等类型的应用,在N版本设备多窗口下运行,建议在onStart/onStop中处理play/pause逻辑,而不要在onResume/onPause方法中处理,以保证:
     * 1、 视频类应用在失去焦点时,能正常播放
     * android:resizeableActivity="false" />
     * Intent intent=newInten
     * 2、 社交类应用在失去焦点时,能继续收到消息、实时刷新
     * 3、 浏览器类应用在失去焦点时,能正常播放网页视频、动画等其他类型的应用,在保证用户体验的前提下,可根据自身业务逻辑处理。
     */
    @Override
    protected void onPause() {
        super.onPause();
        boolean inMultiWindowMode = this.isInMultiWindowMode();
        boolean inPictureInPictureMode = this.isInPictureInPictureMode();
        this.onPictureInPictureModeChanged(true);
        if (inMultiWindowMode) {
            /*如果是分屏模式 一些暂停播放的操作不能在这里执行应该换到onStop()*/
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}

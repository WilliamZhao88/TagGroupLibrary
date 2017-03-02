package egoo.customui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.ArrayList;

import egoo.customui.service.ServerPushService;
import me.codeboy.android.aligntextview.AlignTextView;
import me.codeboy.android.aligntextview.CBAlignTextView;
import me.codeboy.common.base.net.CBHttp;

public class MainActivity extends AppCompatActivity {

    private TagGroup tagGroup;
    private ArrayList mTagList = new ArrayList();

    private TextView mTextViewTv;
    private TextView mAlignTv;
    private TextView mJustifyTv;
    private CBAlignTextView mCbAlignTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tagGroup = (TagGroup) findViewById(R.id.tag_group);

        mTagList.add("服务态度好");
        mTagList.add("解决方案棒");
        mTagList.add("处理效率高");
        mTagList.add("小萌物");
        mTagList.add("高颜值");
        mTagList.add("美女坐席");
        mTagList.add("一般般");
        mTagList.add("太棒了");
        mTagList.add("服务一流");
        mTagList.add("牛人");

        tagGroup.setTags(mTagList);

        mTextViewTv = (TextView) findViewById(R.id.text_view);
        mJustifyTv = (TextView) findViewById(R.id.justify_text_view);
        mAlignTv = (TextView) findViewById(R.id.align_text_view);
        mCbAlignTv = (CBAlignTextView) findViewById(R.id.cb_align_text_view);

        final String text = "这是一段中英文混合的文本，I am very happy today。 aaaaaaaaaa," +
                "测试TextView文本对齐\n\nAlignTextView可以通过setAlign()方法设置每一段尾行的对齐方式, 默认尾行居左对齐. " +
                "CBAlignTextView可以像原生TextView一样操作, 但是需要使用getRealText()获取文本, 欢迎访问open.codeboy.me";
        mTextViewTv.setText(text);
        mJustifyTv.setText(text);
        mAlignTv.setText(text);
//        mCbAlignTv.setPunctuationConvert(true);
        mCbAlignTv.setText(text);

        mAlignTv.setMovementMethod(new ScrollingMovementMethod());

        this.startService(new Intent(this,ServerPushService.class));
    }
}

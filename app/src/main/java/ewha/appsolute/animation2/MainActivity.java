package ewha.appsolute.animation2;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    View mainView;
    ImageView arrowImage;
    AnimationDrawable arrowAnimation;
    boolean cardViewSide;

    enum FLIP_DIRECTION {
        LEFT, RIGHT, UP, DOWN
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainView = findViewById(R.id.view_main);
        setContentView(R.layout.activity_main);

        arrowImage = findViewById(R.id.image_arrow);
        //Arrow 이미지에 Drawable resource 할당
        arrowImage.setBackgroundResource(R.drawable.arrow_animation);
        arrowAnimation = (AnimationDrawable) arrowImage.getBackground();

        //Arrow 이미지 대한 이벤트 추가
        arrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //실행 여부를 확인
                if(arrowAnimation.isRunning()) {
                    arrowAnimation.stop();
                } else {
                    arrowAnimation.start();
                }
            }
        });

        //------------------------------------------------------------------------------------------

        //CardView를 표시할 Fragment에 앞면 할당
        cardViewSide = true;
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new MainActivity.CardFrontFragment())
                    .commit();
        }

        //Fragment Touch 이벤트 처리하기
        findViewById(R.id.container).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //터치 좌표 받아오기
                float coordX = event.getX();
                float coordY = event.getY();

                //Fragment 사이즈 받아오기
                int width = v.getWidth();
                int height = v.getHeight();

                //뷰를 뒤집기 위한 방향 계산
                FLIP_DIRECTION direction;
                if (coordX < width*0.3) {
                    direction = FLIP_DIRECTION.LEFT;
                } else if (coordX > width*0.7) {
                    direction = FLIP_DIRECTION.RIGHT;
                } else {
                    if (coordY < height*0.3) {
                        direction = FLIP_DIRECTION.UP;
                    } else if (coordY > height*0.7) {
                        direction = FLIP_DIRECTION.DOWN;
                    } else {
                        return false;
                    }
                }

                //뷰 뒤집기
                flipCard(direction);
                return false;
            }
        });

    }

    //앞면 레이아웃 inflate
    public static class CardFrontFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_card_front, container, false);
        }
    }

    //면 레이아웃 inflate
    public static class CardBackFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_card_back, container, false);
        }
    }

    private void flipCard(FLIP_DIRECTION direction) {
        if (!cardViewSide) {
            //다시 앞면으로 돌아감 //애니메이션 방향 반대 //뒤로가기 누르는 것과 같은 액션
            cardViewSide = true;
            getSupportFragmentManager().popBackStack();
            return;
        }

        int enter, exit, popEnter, popExit;
        //방향에 따른 애니메이션 설정
        switch(direction) {
            case LEFT:
                enter = R.animator.card_flip_left_in;
                exit = R.animator.card_flip_left_out;
                popEnter = R.animator.card_flip_right_in;
                popExit = R.animator.card_flip_right_out;
                break;
            case RIGHT:
                enter = R.animator.card_flip_right_in;
                exit = R.animator.card_flip_right_out;
                popEnter = R.animator.card_flip_left_in;
                popExit = R.animator.card_flip_left_out;
                break;
            case UP:
                enter = R.animator.card_flip_up_in;
                exit = R.animator.card_flip_up_out;
                popEnter = R.animator.card_flip_down_in;
                popExit = R.animator.card_flip_down_out;
                break;
            default:
                enter = R.animator.card_flip_down_in;
                exit = R.animator.card_flip_down_out;
                popEnter = R.animator.card_flip_up_in;
                popExit = R.animator.card_flip_up_out;
                break;
        }

        cardViewSide = false;
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(enter, exit, popEnter, popExit)
                .replace(R.id.container, new CardBackFragment())
                .addToBackStack(null)
                .commit();

    }
}
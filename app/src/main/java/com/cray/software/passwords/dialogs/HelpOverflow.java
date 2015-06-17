package com.cray.software.passwords.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cray.software.passwords.R;

public class HelpOverflow extends Activity {

    FrameLayout help_container;
    Button button, addButton, viewButton, listButton, viewButtonSkip, addButtonSkip, buttonSkip;
    TextView floatLeft, floatRight, floatSlideLeft, listItem;
    TextView addStartElement, addFloatLeft, addFloatSlideLeft, addFloatRight;
    TextView viewFloat, viewMore, viewDelete, viewCopyHelp;
    Animation topAnimIn, topAnimOut;
    RelativeLayout mainHelp, addHelp, viewHelp, listHelp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setContentView(R.layout.main_help_overflov);

        help_container = (FrameLayout) findViewById(R.id.help_container);
        mainHelp = (RelativeLayout) findViewById(R.id.mainHelp);
        mainHelp.setVisibility(View.GONE);

        addHelp = (RelativeLayout) findViewById(R.id.addHelp);
        addHelp.setVisibility(View.GONE);

        viewHelp = (RelativeLayout) findViewById(R.id.viewHelp);
        viewHelp.setVisibility(View.GONE);

        listHelp = (RelativeLayout) findViewById(R.id.listHelp);
        listHelp.setVisibility(View.GONE);

        Intent helpNumber = getIntent();
        int number = helpNumber.getIntExtra("fromActivity", 0);

        if (number == 1){
            mainHelp.setVisibility(View.VISIBLE);
            floatLeft = (TextView) findViewById(R.id.floatLeft);
            floatLeft.setVisibility(View.GONE);
            floatRight = (TextView) findViewById(R.id.floatRight);
            floatRight.setVisibility(View.GONE);
            floatSlideLeft = (TextView) findViewById(R.id.floatSlideLeft);
            floatSlideLeft.setVisibility(View.GONE);

            buttonSkip = (Button) findViewById(R.id.buttonSkip);
            buttonSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            button = (Button) findViewById(R.id.button);
            button.setVisibility(View.GONE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (floatRight.getVisibility() == View.VISIBLE) {
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                button.setEnabled(false);
                                floatRight.startAnimation(topAnimOut);
                                floatRight.setVisibility(View.GONE);
                            }
                        }, 500);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                                floatLeft.setVisibility(View.VISIBLE);
                                floatLeft.startAnimation(topAnimIn);
                                button.setEnabled(true);
                            }
                        }, 900);
                    } else if (floatLeft.getVisibility() == View.VISIBLE){
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                button.setEnabled(false);
                                floatLeft.startAnimation(topAnimOut);
                                floatLeft.setVisibility(View.GONE);
                            }
                        }, 500);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                                floatSlideLeft.setVisibility(View.VISIBLE);
                                floatSlideLeft.startAnimation(topAnimIn);
                            }
                        }, 900);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                button.setText(R.string.view_act_item_close_in);
                                button.setEnabled(true);
                            }
                        }, 1400);
                    } else if (floatSlideLeft.getVisibility() == View.VISIBLE){
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                floatSlideLeft.startAnimation(topAnimOut);
                                floatSlideLeft.setVisibility(View.GONE);
                            }
                        }, 500);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                button.startAnimation(topAnimOut);
                                button.setVisibility(View.GONE);
                            }
                        }, 1000);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1500);
                    }
                }
            });
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                    floatRight.setVisibility(View.VISIBLE);
                    floatRight.startAnimation(topAnimIn);
                }
            }, 500);

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                    button.setVisibility(View.VISIBLE);
                    button.startAnimation(topAnimIn);
                }
            }, 900);
        } else if (number == 2){
            addHelp.setVisibility(View.VISIBLE);
            addStartElement = (TextView) findViewById(R.id.addStartElement);
            addStartElement.setVisibility(View.GONE);
            addFloatLeft = (TextView) findViewById(R.id.addFloatLeft);
            addFloatLeft.setVisibility(View.GONE);
            addFloatSlideLeft = (TextView) findViewById(R.id.addFloatSlideLeft);
            addFloatSlideLeft.setVisibility(View.GONE);
            addFloatRight = (TextView) findViewById(R.id.addFloatRight);
            addFloatRight.setVisibility(View.GONE);

            addButtonSkip = (Button) findViewById(R.id.addButtonSkip);
            addButtonSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            addButton = (Button) findViewById(R.id.addButton);
            addButton.setVisibility(View.GONE);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (addStartElement.getVisibility() == View.VISIBLE){
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                addButton.setEnabled(false);
                                addStartElement.startAnimation(topAnimOut);
                                addStartElement.setVisibility(View.GONE);
                            }
                        }, 500);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                                addFloatLeft.setVisibility(View.VISIBLE);
                                addFloatLeft.startAnimation(topAnimIn);
                                addButton.setEnabled(true);
                            }
                        }, 900);
                    } else if (addFloatLeft.getVisibility() == View.VISIBLE){
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                addButton.setEnabled(false);
                                addFloatLeft.startAnimation(topAnimOut);
                                addFloatLeft.setVisibility(View.GONE);
                            }
                        }, 500);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                                addFloatSlideLeft.startAnimation(topAnimIn);
                                addFloatSlideLeft.setVisibility(View.VISIBLE);
                                addButton.setEnabled(true);
                            }
                        }, 900);
                    } else if (addFloatSlideLeft.getVisibility() == View.VISIBLE){
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                addButton.setEnabled(false);
                                addFloatSlideLeft.startAnimation(topAnimOut);
                                addFloatSlideLeft.setVisibility(View.GONE);
                            }
                        }, 500);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                                addFloatRight.startAnimation(topAnimIn);
                                addFloatRight.setVisibility(View.VISIBLE);
                            }
                        }, 900);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addButton.setText(R.string.view_act_item_close_in);
                                addButton.setEnabled(true);
                            }
                        }, 1100);
                    } else if (addFloatRight.getVisibility() == View.VISIBLE){
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                addFloatRight.startAnimation(topAnimOut);
                                addFloatRight.setVisibility(View.GONE);
                            }
                        }, 500);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                addButton.startAnimation(topAnimOut);
                                addButton.setVisibility(View.GONE);
                            }
                        }, 900);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1400);
                    }
                }
            });
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                    addStartElement.setVisibility(View.VISIBLE);
                    addStartElement.startAnimation(topAnimIn);
                }
            }, 500);

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                    addButton.setVisibility(View.VISIBLE);
                    addButton.startAnimation(topAnimIn);
                }
            }, 900);
        } else if (number == 3){
            viewHelp.setVisibility(View.VISIBLE);
            viewFloat = (TextView) findViewById(R.id.viewFloat);
            viewFloat.setVisibility(View.GONE);
            viewMore = (TextView) findViewById(R.id.viewMore);
            viewMore.setVisibility(View.GONE);
            viewDelete = (TextView) findViewById(R.id.viewDelete);
            viewDelete.setVisibility(View.GONE);
            viewCopyHelp = (TextView) findViewById(R.id.viewCopyHelp);
            viewCopyHelp.setVisibility(View.GONE);

            viewButtonSkip = (Button) findViewById(R.id.viewButtonSkip);
            viewButtonSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            viewButton = (Button) findViewById(R.id.viewButton);
            viewButton.setVisibility(View.GONE);
            viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewCopyHelp.getVisibility() == View.VISIBLE){
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                viewButton.setEnabled(false);
                                viewCopyHelp.startAnimation(topAnimOut);
                                viewCopyHelp.setVisibility(View.GONE);
                            }
                        }, 500);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                                viewMore.setVisibility(View.VISIBLE);
                                viewMore.startAnimation(topAnimIn);
                            }
                        }, 900);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewButton.setEnabled(true);
                            }
                        }, 1400);
                    } else if (viewMore.getVisibility() == View.VISIBLE){
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                viewButton.setEnabled(false);
                                viewMore.startAnimation(topAnimOut);
                                viewMore.setVisibility(View.GONE);
                            }
                        }, 500);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                                viewFloat.setVisibility(View.VISIBLE);
                                viewFloat.startAnimation(topAnimIn);
                            }
                        }, 900);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewButton.setEnabled(true);
                            }
                        }, 1400);
                    } else if(viewFloat.getVisibility() == View.VISIBLE){
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                viewFloat.startAnimation(topAnimOut);
                                viewFloat.setVisibility(View.GONE);
                            }
                        }, 500);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                                viewDelete.setVisibility(View.VISIBLE);
                                viewDelete.startAnimation(topAnimIn);
                            }
                        }, 900);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewButton.setText(R.string.view_act_item_close_in);
                                viewButton.setEnabled(true);
                            }
                        }, 1400);
                    } else if (viewDelete.getVisibility() == View.VISIBLE){
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                viewDelete.startAnimation(topAnimOut);
                                viewDelete.setVisibility(View.GONE);
                            }
                        }, 500);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                viewButton.startAnimation(topAnimOut);
                                viewButton.setVisibility(View.GONE);
                            }
                        }, 1000);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1500);
                    }
                }
            });
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                    viewCopyHelp.setVisibility(View.VISIBLE);
                    viewCopyHelp.startAnimation(topAnimIn);
                }
            }, 500);

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                    viewButton.setVisibility(View.VISIBLE);
                    viewButton.startAnimation(topAnimIn);
                }
            }, 900);
        } else if (number == 4){
            listHelp.setVisibility(View.VISIBLE);
            listItem = (TextView) findViewById(R.id.listItem);
            listItem.setVisibility(View.GONE);

            listButton = (Button) findViewById(R.id.listButton);
            listButton.setVisibility(View.GONE);
            listButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listItem.getVisibility() == View.VISIBLE){
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                listItem.startAnimation(topAnimOut);
                                listItem.setVisibility(View.GONE);
                            }
                        }, 400);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                topAnimOut = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_out);
                                listButton.startAnimation(topAnimOut);
                                listButton.setVisibility(View.GONE);
                            }
                        }, 700);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 900);
                    }
                }
            });
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                    listItem.setVisibility(View.VISIBLE);
                    listItem.startAnimation(topAnimIn);
                }
            }, 500);

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    topAnimIn = AnimationUtils.loadAnimation(HelpOverflow.this, R.anim.help_tip_top_in);
                    listButton.setVisibility(View.VISIBLE);
                    listButton.startAnimation(topAnimIn);
                }
            }, 900);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return MotionEvent.ACTION_OUTSIDE != event.getAction() && super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
    }
}

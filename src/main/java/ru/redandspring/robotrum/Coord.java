package ru.redandspring.robotrum;

/**
 * Created by Alexander Tretyakov.
 */
public enum Coord {

    ENTER_TROOM_FIELD(770, 410),
    ETROOM_FIELD(380, 200),

    NEXT_BUTTON(1095, 683),
    HAVE_TROOM_RADIO_BUTTON(680, 420),
    FA2_DISABLE_RADIO_BUTTON(680, 440),

    HARVESTER_CHECK_COLOR(770, 410),
    AIRSHIP_CHECK_COLOR(400, 410),
    FA2_CHECK_COLOR(1021, 461),
    ENTER_TROOM_FIELD_COLOR(1021, 415),

    GENERATE_WALTROOM_WINDOW(700, 680),

    ETROOM_RUN(170, 1025);

    public int x;
    public int y;

    Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

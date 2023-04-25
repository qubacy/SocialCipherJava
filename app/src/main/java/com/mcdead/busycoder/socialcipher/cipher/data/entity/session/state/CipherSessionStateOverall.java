package com.mcdead.busycoder.socialcipher.cipher.data.entity.session.state;

public enum CipherSessionStateOverall {
    /*

    For A:
    generated PUBLIC, generated A-SIDE-SECRET, generated A-SIDE-PUBLIC,
    sent PUBLIC and A-SIDE-PUBLIC to B;

    For Xn:
    received PUBLIC and PREV-SIDE-PUBLIC, generated Xn-SIDE-SECRET,
    generated A-Xn-SIDE-PUBLIC, sent A-Xn-SIDE-PUBLIC to Xn+1;

    For N:
    received PUBLIC and PREV-SIDE-PUBLIC, generated N-SIDE-SECRET,
    generated A-X-N-SIDE-PUBLIC, sent A-N-SIDE-PUBLIC to Xn+1;

    */
    INIT(),
    /*



     */
    SET();
}

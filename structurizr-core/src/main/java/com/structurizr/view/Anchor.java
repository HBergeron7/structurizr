package com.structurizr.view;

public enum Anchor {

    Center,         //default anchor at center of view bbox
    ModelCenter,    //anchor at center of model bbox
    Perpendicular,  //anchor that ensures an orthogonal route to the other endpoint
    MidSide         //anchor in the middle of the side of view bbox closest to the other endpoint

}

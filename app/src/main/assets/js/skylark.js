(function() {
    //start

}());

//Android Test
function showAndroidToast(toast) {
    Android.showToast(toast);
}

function androidButtonTest(){
    console.log("console: It's a TEST!");
    showAndroidToast("showAndroidToast: It's a TEST!");
    Android.runCamera(false);
}

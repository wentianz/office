const ticket ='HoagFKDcsGMVCIY2vOjf9v6M9KXW3KWy3Onjj7-vHwYJLnQvaa6elbOxiCYJlpF9mfu6VTB7lARdTWhf_lbb6g'
const  timestamp=Date.now();
const nonceStr = Math.random().toString(16).substr(2);
const urlStr = location.href;
console.log(urlStr);
const originParams = 'jsapi_ticket=' + ticket
    + '&noncestr=' + nonceStr
    + '&timestamp=' + timestamp
    + '&url=' + urlStr;

var shaObj = new jsSHA("SHA-1", "TEXT");
shaObj.update(originParams);
var signature = shaObj.getHash("HEX");
console.log(signature);
wx.config({
    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
    appId: 'wxed6e800308e446f7', // 必填，公众号的唯一标识
    timestamp:timestamp , // 必填，生成签名的时间戳
    nonceStr: nonceStr, // 必填，生成签名的随机串
    signature: signature,// 必填，签名
    jsApiList: [
        'checkJsApi',
        'chooseImage',
        'getLocation',
        'openLocation',
        'startRecord',
        'scanQRCode'] // 必填，需要使用的JS接口列表
});

wx.error(function (error) {
    console.error(error);
});

wx.ready(function () {
    console.log('验证成功');
});

var app = new Vue({
    el: '#app',
    data: {},
    mounted: function () {

    },
    methods: {
        checkJsApi() {
            console.log('checkJsApi click');
            wx.checkJsApi({
                jsApiList: ['chooseImage'], // 需要检测的JS接口列表，所有JS接口列表见附录2,
                success: function (res) {
                    console.log(res);
                }, fail: function (error) {
                    console.error(error);
                }
            });
        },
        up(){
            wx.startRecord();
        },
        scanQRCode(){
            wx.scanQRCode({
                needResult: 0, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
                scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
                success: function (res) {
                    var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
                }
            });
        },
        chooseImage(){
            wx.chooseImage({
                count: 1, // 默认9
                sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
                sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
                success: function (res) {
                    var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
                }
            });
        },
        openLocation(){
            var latitude=0;
            var longitude=0;
            var accuracy;
            wx.getLocation({
                type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
                success: function (res) {
                    console.log(res)
                    latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
                    longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
                    var speed = res.speed; // 速度，以米/每秒计
                     accuracy = res.accuracy; // 位置精度
                }
            });
            console.log("经纬度"+latitude,longitude)
            wx.openLocation({
                latitude: latitude, // 纬度，浮点数，范围为90 ~ -90
                longitude: longitude, // 经度，浮点数，范围为180 ~ -180。
                name: '', // 位置名
                address: '', // 地址详情说明
                scale: 1, // 地图缩放级别,整形值,范围从1~28。默认为最大
                infoUrl: '' // 在查看位置界面底部显示的超链接,可点击跳转
            });

        },



    }


});
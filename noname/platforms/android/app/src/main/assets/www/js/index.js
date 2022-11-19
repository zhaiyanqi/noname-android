'use strict';
(function () {
    var preapp = {
        url: String,
        initialize: function () {
            this.initJavaArgs();
            this.start();
        },
        initJavaArgs: function () {
            this.url = window.jsBridge.getGamePath();
            console.log("load, this.url: " + this.url);

            if (localStorage.getItem("noname_inited") != this.url) {
                localStorage.setItem('noname_inited', this.url);
            }
        },
        afterLoad: function() {
            console.log("afterLoad ");
        },
        start: function () {
            var url = this.url;
            var loadFailed = function () {
                window.location.reload();
            }

            var load = function (src, onload, onerror) {
                console.log("load, src: " + src);

                var script = document.createElement('script');
                script.src = url + 'game/' + src + '.js';
                script.onload = onload;
                script.onerror = onerror;
                document.head.appendChild(script);
            }

            var afterLoad = this.afterLoad;

            load('update', function () {
                load('config', function () {
                    load('package', function () {
                        load('game', afterLoad, loadFailed);
                    }, loadFailed);
                }, loadFailed);
            }, loadFailed);
        }
    };

    preapp.initialize();
}());

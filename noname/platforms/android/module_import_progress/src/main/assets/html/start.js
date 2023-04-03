'use strict';
(function () {
    var lib = {
        configprefix: 'noname_0.9_'

    };

    var cache = {};

    var app = {
        initialize: function () {
            this.jsReady();
            // this.javaReady();
            this.openDB();
        },
        jsReady: function () {
            console.log("start: " + window.localStorage);
        },
        openDB: function () {
            if (lib.db) {
                console.log("db is not close, close first");
                lib.db.close(function() {
                    lib.db = null;
                    this.openDB();
                });
                return;
            }

            var request = window.indexedDB.open(lib.configprefix + 'data', 4);
            request.onupgradeneeded = function (e) {
                var db = e.target.result;
                if (!db.objectStoreNames.contains('video')) {
                    db.createObjectStore('video', { keyPath: 'time' });
                }
                if (!db.objectStoreNames.contains('image')) {
                    db.createObjectStore('image');
                }
                if (!db.objectStoreNames.contains('audio')) {
                    db.createObjectStore('audio');
                }
                if (!db.objectStoreNames.contains('config')) {
                    db.createObjectStore('config');
                }
                if (!db.objectStoreNames.contains('data')) {
                    db.createObjectStore('data');
                }
            };
            request.onsuccess = function (e) {
                lib.db = e.target.result;
                console.log("openDB success.");
            }
            if(!localStorage.getItem('noname_alerted')){
                localStorage.setItem('noname_alerted',true);
            }
        },
        getDB: function (key, callback) {
            if (!lib.db) {
                console.log("getDB, lib.db: " + lib + ", key: " + key);
                return;
            }

            if (key) {
                // 如果缓存里面有，就从缓存里面取
                if(cache.hasOwnProperty(key)) {
                    var value = cache[key];
                    console.log("cache get：key = " +key + " ,value = " + value);
                    callback(value);
                    return;
                }
                var store = lib.db.transaction(['config'], 'readwrite').objectStore('config');
                store.get(key).onsuccess = function (e) {
                    cache[key] = e.target.result;
                    callback(e.target.result)
                };
            }
        },
        putDB: function (key, value, onsucc) {
            if (!lib.db) {
                console.log("putDB, lib.db: " + lib + ", key: " + key + ", value: " + value);
                return;
            }
            // 及时给缓存添加上
            console.log("cache set, success, key: " + key + ", value: " + value);
            cache[key] = value;

            var put = lib.db.transaction(['config'], 'readwrite').objectStore('config').put(value, key);
            put.onsuccess = function () {
                console.log("putDB, success, key: " + key + ", value: " + value);

                if (onsucc) {
                    onsucc();
                }
            };
        },
        closeDB: function() {
            if (lib.db) {
                lib.db.close();
                lib.db = null;
            }
        },
        addExtension: function(extname, enable){
            this.getDB("extensions", function (value) {
                if(!Array.isArray(value)) value = [];
                console.log("开始添加："+extname+enable);
                if(value.indexOf(extname) > -1) {
                    console.log("已经添加过："+extname);
                    return window.jsBridge.onAddExtension(true, extname);
                }
                value.push(extname);
                app.putDB("extensions", value, function(){
                    app.enableExtension(extname, true, function(){
                        console.log(extname+"添加成功！");
                        window.jsBridge.onAddExtension(true, extname);
                    });
                });
            });
        },
        removeExtension: function(extname){
            this.getDB("extensions", function (value) {
                if(!Array.isArray(value)) value = [];
                var index = value.indexOf(extname)
                if(index === -1) {
                    console.log("该扩展已不存在："+extname);
                    return window.jsBridge.onRemoveExtension(true, extname);
                }
                value.splice(index, 1);
                app.putDB("extensions", value, function(){
                    app.enableExtension(extname, false, function(){
                        window.jsBridge.onRemoveExtension(true, extname);
                    });
                });
            });
        },
        enableExtension: function (extname, enable, onsucc) {
            var key = "extension_" + extname + "_enable";
            if(typeof onsucc !== 'function'){
                onsucc = function(){
                    window.jsBridge.onEnableExtension(extname, enable);
                }
            }
            this.putDB(key, enable, onsucc);
        },
    };

    app.initialize();
    window.app = app;
}());

YAHOO.namespace('ForexInvest.Main');
(function () {
    //define a few shortcuts
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Connect = YAHOO.util.Connect;
    var jsdir = 'main/js/';
    var mainComp = {};
    var systemMonitorTabId = 'systemMonitor';
    var clientManagerTabId = 'clientManager';
    var marketDataTabId = 'marketData';
    YAHOO.ForexInvest.Main = {
        mainComp : mainComp,
        init : function () {
            mainComp.layout = new YAHOO.widget.Layout({
                minWidth : 1000,
                units : [
                    { position : 'top', height : 45, resize : false, body : 'top1' },
                   /* { position : 'right', width : 300, body : 'right1', header : 'Logger Console', gutter : '0 5 0 5px', minWidth : 190 },*/
                    { position : 'left', width : 0, resize : true, body : 'left1', gutter : '0 5 0 5px', minWidth : 0 },
                    { position : 'center', gutter : '0 5px 0 2' }
                ]
            });
            //On resize, resize the left and right column content
            mainComp.layout.on('resize', function () {
                var l = this.getUnitByPosition('left');
                var th = l.get('height') - YAHOO.util.Dom.get('folder_top').offsetHeight;
                var h = th - 4; //Borders around the 2 areas
                h = h - 9; //Padding between the 2 parts
                YAHOO.util.Dom.setStyle('folder_list', 'height', h + 'px');
            }, mainComp.layout, true);
            //On render, load tabview.js and button.js
            mainComp.layout.on('render', function () {
                YAHOO.ForexInvest.Main.initTabs();
                /*mainComp.layout.getUnitByPosition('right').collapse();*/
                setTimeout(function () {
                    YAHOO.util.Dom.setStyle(document.body, 'visibility', 'visible');
                    mainComp.layout.resize();
                }, 1000);
            });
            //Render the layout
            mainComp.layout.render();
        },

        initTabs : function () {
            mainComp.tabView = new YAHOO.widget.TabView();
            //Method to Resize the tabview
            var resizeTabView = function () {
                var ul = mainComp.tabView._tabParent.offsetHeight;
                Dom.setStyle(mainComp.tabView._contentParent, 'height', ((mainComp.layout.getSizes().center.h - ul) - 2) + 'px');
            };
            //Create the System Monitor tab
            mainComp.tabView.addTab(new YAHOO.widget.Tab({
                //Inject a span for the icon
                label : '<span></span>System Monitor',
                id : systemMonitorTabId,
                content : '<div id="' + systemMonitorTabId +'-tabContainer"></div>'
            }));
            //Create the Client - Manager tab
            mainComp.tabView.addTab(new YAHOO.widget.Tab({
                //Inject a span for the icon
                label : '<span></span>Client Manager',
                id : clientManagerTabId,
                content : ''

            }));

            //Create the Chart tab
            mainComp.tabView.addTab(new YAHOO.widget.Tab({
                //Inject a span for the icon
                label : '<span></span>Market Data Chart',
                id : marketDataTabId,
                content : '<div id="' + marketDataTabId +'-tabContainer"></div>'

            }));

            mainComp.tabView.on('activeTabChange', function (ev) {
                //Tabs have changed
                if (ev.newValue.get('id') == systemMonitorTabId) {
                    YAHOO.ForexInvest.Main.initSystemMonitorTab();
                }
                if (ev.newValue.get('id') == clientManagerTabId) {
                    YAHOO.ForexInvest.Main.initClientManagerTab();
                }
                if (ev.newValue.get('id') == marketDataTabId) {
                    YAHOO.ForexInvest.Main.initMarketDataTab();
                }
                //Resize to fit the new content
                mainComp.layout.resize();
            });

            //Add the tabview to the center unit of the main layout
            var el = mainComp.layout.getUnitByPosition('center').get('wrap');
            mainComp.tabView.appendTo(el);
            mainComp.tabView.selectTab(0);
            resizeTabView();
        },


        initSystemMonitorTab : function () {  //systemMonitorTabId
            var tabContainer = Dom.get(systemMonitorTabId +'-tabContainer');
            tabContainer.innerHTML = '';
            new YAHOO.ForexInvest.SystemConsole(tabContainer).init();
        },
        initClientManagerTab : function () {  //clientManagerTabId
            //Dom.get("systemMonitorTabContainer").innerHTML += " Test";
            //todo cmeng
        },
        initMarketDataTab: function () {  //clientManagerTabId
            var tabContainer = Dom.get(marketDataTabId +'-tabContainer');
            tabContainer.innerHTML = '';
            //new YAHOO.ForexInvest.Chart(tabContainer).init();
            new YAHOO.ForexInvest.PerformanceTab(tabContainer).init();

        }

    };


})();


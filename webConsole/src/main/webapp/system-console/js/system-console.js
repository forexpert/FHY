YAHOO.namespace('ForexInvest.SystemConsole');
(function () {
    //define a few shortcuts
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Connect = YAHOO.util.Connect;
    var jsonHelper = YAHOO.lang.JSON;

    YAHOO.ForexInvest.SystemConsole = function (container) {
        this.container = container;
    };
    YAHOO.ForexInvest.SystemConsole.prototype = {
        init : function () {
            // Instantiate a Panel from markup
            var that = this;
            Connect.asyncRequest('get', '/app/tab/system-console', {
                success: that.initPanels,
                failure: null,
                argument: { that: that }
            }, null);

            //this.jsmServerPanel.render(this.container);

            /*// Instantiate a Panel from script
            YAHOO.example.container.panel2 = new YAHOO.widget.Panel("panel2", { width : "320px", visible : false, draggable : false, close : false });
            YAHOO.example.container.panel2.setHeader("Panel #2 from Script");
            YAHOO.example.container.panel2.setBody("This is a dynamically generated Panel.");
            YAHOO.example.container.panel2.setFooter("End of Panel #2");
            YAHOO.example.container.panel2.render("container");*/
        },

        initPanels : function(response){
            var that = response.argument.that;
            that.container.innerHTML = response.responseText;

            that.initComponentInfoPanel();
            //that.initWatch Client InfoPanel();
            //that.initMarket Data InfoPanel();
            //that.initRobot Client InfoPanel();
        },

        /**
         *  initComponentInfoPanel:
         *  1. update CPU/Memory, Components running status,
         *  2. add event to buttons to start/stop components
         */
        initComponentInfoPanel : function(){
            var componentInfoPanelContentEL = Selector.query(".system-console .system-panel.component .panel-content",
                this.container, true);
            if(componentInfoPanelContentEL){
                var that = this;
                Connect.asyncRequest('get', '/app/system-console/ComponentInfo', {
                    success: function(response){
                        var json = jsonHelper.parse(response.responseText);
                        var cpuEl = Selector.query("span.js-cpu-usage", componentInfoPanelContentEL, true);
                        var memEl = Selector.query("span.js-mem-usage", componentInfoPanelContentEL, true);
                        var activemqEl = Selector.query("span.js-activemq-server", componentInfoPanelContentEL, true);
                        cpuEl.innerHTML = json.cpuUsage;
                        memEl.innerHTML = json.memUsage;
                        Dom.removeClass(activemqEl, json.isJMSServerRunning?"status-down": "status-up");
                        Dom.addClass(activemqEl, json.isJMSServerRunning?"status-up": "status-down");
                    },
                    failure: null,
                    argument: { that: that }
                }, null);
            }

        }
    }

})();


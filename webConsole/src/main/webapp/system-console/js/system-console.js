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
                success : that.initPanels,
                failure : null,
                argument : { that : that }
            }, null);

            //this.jsmServerPanel.render(this.container);

            /*// Instantiate a Panel from script
             YAHOO.example.container.panel2 = new YAHOO.widget.Panel("panel2", { width : "320px", visible : false, draggable : false, close : false });
             YAHOO.example.container.panel2.setHeader("Panel #2 from Script");
             YAHOO.example.container.panel2.setBody("This is a dynamically generated Panel.");
             YAHOO.example.container.panel2.setFooter("End of Panel #2");
             YAHOO.example.container.panel2.render("container");*/
        },

        initPanels : function (response) {
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
        initComponentInfoPanel : function () {
            var componentInfoPanelContentEL = Selector.query(".system-console .system-panel.component .panel-content",
                this.container, true);
            this.startActiveMQButton = new YAHOO.widget.Button(Selector.query("li.activemq-server button", componentInfoPanelContentEL, true));
            this.startMockBrokerServerButton = new YAHOO.widget.Button(Selector.query("li.mock-broker-server", componentInfoPanelContentEL, true));
            this.startStrategyCenterButton = new YAHOO.widget.Button(Selector.query("li.strategy-center-server", componentInfoPanelContentEL, true));

            this.startActiveMQButton.on("click", function (event) {
                YAHOO.util.Event.preventDefault(event);
                var target = YAHOO.util.Event.getTarget(event);
                var serverName = Dom.getAttribute(target.parentNode, "class");
                Connect.asyncRequest('post', '/app/system-console/startComponent', {
                    success : null,
                    failure : null
                }, "serverName=" + serverName);
            });


            if (componentInfoPanelContentEL) {
                var that = this;
                Connect.asyncRequest('get', '/app/system-console/ComponentInfo', {
                    success : function (response) {
                        var json = jsonHelper.parse(response.responseText);
                        var cpuEl = Selector.query("li.js-cpu-usage span.value", componentInfoPanelContentEL, true);
                        var memEl = Selector.query("li.js-mem-usage span.value", componentInfoPanelContentEL, true);
                        var activemqEl = Selector.query("li.activemq-server", componentInfoPanelContentEL, true);
                        var mockBrokerServerEl = Selector.query("li.mock-broker-server", componentInfoPanelContentEL, true);
                        var strategyCenterEl = Selector.query("li.strategy-center-server", componentInfoPanelContentEL, true);
                        cpuEl.innerHTML = json.cpuUsage;
                        memEl.innerHTML = json.memUsage;
                        var updateServerELUI = function (serverEl, isRunning) {
                            var statusEL = Selector.query("span.server-status", serverEl, true);
                            var buttonEL = Selector.query("button", serverEl, true);
                            Dom.removeClass(statusEL, isRunning ? "down" : "up");
                            Dom.addClass(statusEL, isRunning ? "up" : "down");
                            isRunning ? Dom.addClass(buttonEL, "hide") : Dom.removeClass(buttonEL, "hide");
                        }
                        updateServerELUI(activemqEl, json.isJMSServerRunning);
                        updateServerELUI(mockBrokerServerEl, json.isMockBrokerServerRunning);
                        updateServerELUI(strategyCenterEl, json.isStrategyServerRunning);
                    },
                    failure : null,
                    argument : { that : that }
                }, null);
            }

        }
    }

})();


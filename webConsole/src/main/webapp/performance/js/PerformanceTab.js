YAHOO.namespace('ForexInvest.PerformanceTab');
(function () {
    //define a few shortcuts
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Connect = YAHOO.util.Connect;
    var jsonHelper = YAHOO.lang.JSON;

    YAHOO.ForexInvest.PerformanceTab = function (container) {
        this.container = container;
    };
    YAHOO.ForexInvest.PerformanceTab.prototype = {
        init : function () {
            this.container.innerHTML =
                "<div class='performance-container'>" +
                    "<div class='performance-data'><div class='baseInfo'></div><div class='equityChart'></div><div class='closePositions'></div></div>" +
                    "<div class='control'>" +
                        "<div class='buttons'>" +
                            "<p><button class='startTest'>Start Back Testing</button></p>" +
                            "<p><input type='text' class='from' value='2011.03.21 00:00:00 +0000'>From</input>" +
                            "<input type='text' class='to' value='2012.03.21 00:00:10 +0000'>To</input></p>" +
                            "<p> Status<div class='status'>Not started!</div></p>" +


                        "</div>" +
                        "<div class='clientPanel'>" +

                        "</div>" +

                        "<div class='strategyPanel'>" +

                        "</div>" +
                    "</div>" +

                "</div>";

            this.clientPanel = Selector.query(".performance-container .clientPanel", this.container, true);
            this.strategyPanel = Selector.query(".performance-container .strategyPanel", this.container, true);
            this.buttonPanel = Selector.query(".performance-container .control .buttons", this.container, true);
            this.startTestButtonEL = Selector.query(".startTest", this.buttonPanel, true);
            Event.addListener(this.startTestButtonEL, "click", this.startTest, this, true);
            this.testFromEL = Selector.query(".from", this.buttonPanel, true);
            this.testToEL = Selector.query(".to", this.buttonPanel, true);
            this.status = Selector.query(".status", this.buttonPanel, true);
            this.initClientAndStrategyPanel();
            this.refreshBackTestingStatus();
        },

        updateStatusText: function(text){
            this.status.innerHTML = text;
        },

        startTest: function(event) {
            Event.preventDefault(event);
            var that = this;
            Connect.asyncRequest('post', '/app/system-console/startBackTesting', {
                success : function (response) {
                    var json = jsonHelper.parse(response.responseText);
                    if(json.StartBackTesting){
                        response.argument.that.updateStatusText("send request to start back testing!")
                    }
                },
                failure : null,
                argument : { that : that }
            }, "from="+encodeURIComponent(this.testFromEL.value)+"&&to="+encodeURIComponent(this.testToEL.value));
        },

        refreshBackTestingStatus: function(){
            var that = this;
            Connect.asyncRequest('get', '/app/system-console/getBackTestingStatus', {
                success : function (response) {
                    var json = jsonHelper.parse(response.responseText);
                    response.argument.that.updateStatusText(json.status?"In Testing...": "Test Not Started");
                    setTimeout(function(){that.refreshBackTestingStatus()}, 3000);
                },
                failure : null,
                argument : { that : that }
            }, null);
        },

        getTestClientPerformanceData: function(clientId){
            var that = this;
            Connect.asyncRequest('get', '/app/system-console/getTestBrokerClientPerformanceData?testBrokerClientId='+clientId, {
                success : function (response) {
                    var json = jsonHelper.parse(response.responseText);
                    var baseInfoEL = Selector.query(".performance-container .performance-data .baseInfo", this.container, true);
                    var equityChartEL = Selector.query(".performance-container .performance-data .equityChart", this.container, true);

                    baseInfoEL.innerHTML = "<p><H1>Client "+ json.testClientName +" Performance Report</H1></p>" +
                        "<p>Test Strategy is " + json.strategyname + " </p>" +
                        "<p>Start Equity: " + json.equityRecords[0].equity + " </p>" +
                        "<p>End Equity: " + json.equityRecords[json.equityRecords.length-1].equity + " </p>";

                    YAHOO.ForexInvest.Chart.drawEquityChart(json.equityRecords, equityChartEL);
                    that.initClosePositionLink(clientId);
                    //that.initEquityChart(json.equityRecords);

                },
                failure : null,
                argument : { that : that }
            }, null);
        },



        initClosePositionLink: function(clientId){
            var closePositionsEL = Selector.query(".performance-container .performance-data .closePositions", this.container, true);
            closePositionsEL.innerHTML = "<a href='" + "/app/system-console/downloadClientPerformanceCSV?testBrokerClientId="+clientId+"' target='_blank'>Download Closed Position CSV file</a>"

            //since browser is hard to display so much position elements , we choose to download and view them in excel
            /*var myColumnDefs = [
                {key:"positionId", sortable:true, resizeable:true},
                {key:"openTime", sortable:true, resizeable:true},
                {key:"closeTime", sortable:true, resizeable:true},
                {key:"instrument", sortable:true, resizeable:true},
                {key:"direction", sortable:true, resizeable:true},
                {key:"status", sortable:true, resizeable:true},
                {key:"closeReason", sortable:true, resizeable:true},
                {key:"amount", sortable:true, resizeable:true},
                {key:"openPrice", sortable:true, resizeable:true},
                {key:"closePrice", sortable:true, resizeable:true},
                {key:"stopLossInPips", sortable:true, resizeable:true},
                {key:"takeProfitInPips", sortable:true, resizeable:true}
            ];

            var myDataSource = new YAHOO.util.DataSource(closePositions);
            myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
            myDataSource.responseSchema = {
                fields: ["positionId","openTime","closeTime","instrument","direction","status","closeReason","amount","openPrice","closePrice","stopLossInPips","takeProfitInPips"]
            };

            this.positionTable = new YAHOO.widget.DataTable(closePositionsEL,
                myColumnDefs, myDataSource, {caption:"ClosePosition Table"});
*/
        },

        initClientPanel : function (clients) {
            var myColumnDefs = [
                {key:"ClientId", sortable:true, resizeable:true},
                {key:"StrategyName", sortable:true, resizeable:true}
            ];

            var myDataSource = new YAHOO.util.DataSource(clients);
            myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
            myDataSource.responseSchema = {
                fields: ["ClientId","StrategyName"]
            };

            this.clientTable = new YAHOO.widget.DataTable(this.clientPanel,
                myColumnDefs, myDataSource, {caption:"Test Broker Client List"});
            var that = this;
            var handleClientTableCellClick = function(oArgs){
                var e = oArgs.event;
                var cell = oArgs.target;
                var target = Event.getTarget(e);
                Event.preventDefault(e);

                switch(that.clientTable.getColumn(cell).key){
                    case "ClientId":
                        that.getTestClientPerformanceData(that.clientTable.getRecord(target).getData("ClientId"));
                        break;
                }
            };
            this.clientTable.subscribe('cellClickEvent', handleClientTableCellClick);

        },




        initStrategyPanel : function (strategies) {
            var myColumnDefs = [
                {key:"StrategyName", sortable:true, resizeable:true}
            ];

            var myDataSource = new YAHOO.util.DataSource(strategies);
            myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
            myDataSource.responseSchema = {
                fields: ["StrategyName"]
            };

            new YAHOO.widget.DataTable(this.strategyPanel,
                myColumnDefs, myDataSource, {caption:"StrategyName List"});
        },


        initClientAndStrategyPanel : function () {
            var that = this;
            Connect.asyncRequest('get', '/app/system-console/getBackTestingInfo', {
                success : function (response) {
                    var json = jsonHelper.parse(response.responseText);
                    var clients = json.clients;
                    var strategies = json.strategies;
                    that.initClientPanel(clients);
                    that.initStrategyPanel(strategies);

                },
                failure : null,
                argument : { that : that }
            }, null);
        }
    }
})();
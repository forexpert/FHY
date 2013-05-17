YAHOO.namespace('ForexInvest.Chart');
(function () {
    //define a few shortcuts
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Connect = YAHOO.util.Connect;


    YAHOO.ForexInvest.Chart = function (container) {
        this.container = container;
    };
    YAHOO.ForexInvest.Chart.prototype = {
        init : function () {
            this.container.innerHTML=
                "<div class='chart-container'></div>" +
                "<div class='buttons'>" +
                    "<div class='button-text'>Instruments: </div><div class='button instrument'></div>" +
                    "<div class='button-text'>Period: </div><div class='button period'></div>" +
                    "<div class='button-text'>from: </div><div class='button from'></div>" +
                    "<div class='button-text'>to: </div><div class='button to'></div>" +
                "</div>";

            this.initButtons();

        },

        initButtons : function(){

        },

        example: function(){


            YAHOO.example.monthlyExpenses =
                [
                    { month : "January", rent : 880.00, utilities : 894.68 },
                    { month : "February", rent : 880.00, utilities : 901.35 },
                    { month : "March", rent : 880.00, utilities : 889.32 },
                    { month : "April", rent : 880.00, utilities : 884.71 },
                    { month : "May", rent : 910.00, utilities : 879.811 },
                    { month : "June", rent : 910.00, utilities : 897.95 }
                ];

            var myDataSource = new YAHOO.util.DataSource(YAHOO.example.monthlyExpenses);
            myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
            myDataSource.responseSchema =
            {
                fields : [ "month", "rent", "utilities" ]
            };

            //series definition for Column and Line Charts
            var seriesDef =
                [
                    { displayName : "Rent", yField : "rent" },
                    { displayName : "Utilities", yField : "utilities" }
                ];


            //format currency
            YAHOO.example.formatCurrencyAxisLabel = function (value)
            {
                return YAHOO.util.Number.format(value,
                    {
                        prefix : "$",
                        thousandsSeparator : ",",
                        decimalPlaces : 2
                    });
            };

//return the formatted text
            YAHOO.example.getDataTipText = function (item, index, series, axisField)
            {
                var toolTipText = series.displayName + " for " + item.month;
                toolTipText += "\n" + YAHOO.example.formatCurrencyAxisLabel(item[series[axisField]]);
                return toolTipText;
            };

//DataTip function for the Line Chart and Column Chart
            YAHOO.example.getYAxisDataTipText = function (item, index, series)
            {
                return YAHOO.example.getDataTipText(item, index, series, "yField");
            };

//DataTip function for the Bar Chart
            YAHOO.example.getXAxisDataTipText = function (item, index, series)
            {
                return YAHOO.example.getDataTipText(item, index, series, "xField");
            };

            //create a Numeric Axis for displaying dollars
            var currencyAxis = new YAHOO.widget.NumericAxis();
            currencyAxis.minimum = 800;
            currencyAxis.labelFunction = YAHOO.example.formatCurrencyAxisLabel;


            //Create Line Chart
            var lineChart = new YAHOO.widget.LineChart(this.container.id, myDataSource,
                {
                    series : seriesDef,
                    xField : "month",
                    yAxis : currencyAxis,
                    dataTipFunction : YAHOO.example.getYAxisDataTipText
                });
        }


    }
})();
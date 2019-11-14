import { Component, OnInit, Input } from '@angular/core';
import {ChartOptions, ChartType, ChartDataSets} from 'chart.js'
import { Color } from 'ng2-charts';
import {Position} from '../position'
let dateFormat = require('dateformat');
@Component({
  selector: 'app-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.css']
})
export class ChartComponent implements OnInit {

  bubbleChartType:ChartType = 'bubble';
  bubbleChartLegend:boolean=false;
  bubbleChartColors:Color[]=[];
  bubbleChartOptions:ChartOptions = {
    title: {
      display: true,
      text: 'Timeline'},
    responsive: true,
    tooltips: {
      callbacks: {
        label: this.tooltipLabelFormatter
        }
     },
    scales: {
      xAxes: [{
        ticks: {
          min: 0,
          max: 10,
          display: false
        },
        gridLines: {
              display:false
        }
      }],
      yAxes: [{
        ticks: {
          min: -1,
          max: 1,
          display: false
        },
        gridLines: {
          display:false
    }
      }]
    }
  }
  bubbleChartData:ChartDataSets[]=[{data:[]}];


  constructor() { }

  ngOnInit() {
  }

  //displays the positions in the chart
  @Input()
  set positions(positions:Position[])
  {
    let data:ChartDataSets[]=[];
    let bubbleColors:Color[]=[];
    let max:number=100;
    let min:number=0;
    if(positions.length>0)
    {
      max=positions[0].mark;
      min=positions[0].mark;
    }
    else{
      data=[{data:[]}]
    }
    for (let i=0; i< positions.length; i++)
    {
      if(positions[i].mark>max)
      {
        max=positions[i].mark;
      }
      if(positions[i].mark<min)
      {
        min=positions[i].mark;
      }
      let c= this.colorHasher(positions[i].positionOwner);
      let color:Color = {
        backgroundColor: c,
        borderColor: c,
      }
      let d:ChartDataSets={
        data : [{
          x: positions[i].mark,
          y: 0,
          r: 5
        }]
      }
      data.push(d);
      bubbleColors.push(color);
    }
    let offset=(max-min)/30;
    let options= {
      title: {
        display: true,
        text: 'Timeline'},
      responsive: true,
      tooltips: {
        callbacks: {
          label: this.tooltipLabelFormatter
          }
       },
      scales: {
        xAxes: [{
          ticks: {
            min: min-offset,
            max: max+offset,
            display: false
          },
          gridLines: {
            display:false}
        }],
        yAxes: [{
          ticks: {
            min: -1,
            max: 1,
            display: false
          },
          gridLines: {
            display:false}
        }]
      }
    }

    this.bubbleChartOptions=options;
    this.bubbleChartData=data;
    this.bubbleChartColors=bubbleColors;
  }


  //determine color differently from the one used in the map
  private colorHasher(s:string):string {
    let hash=19;
    for (let i = 0; i < s.length; i++) {
      hash = s.charCodeAt(i) + ((hash << 5) - hash);
      hash = hash & hash;
    }
    let color = '#';
    for (let i = 0; i < 3; i++) {
        let value = (hash >> (i * 8)) & 255;
        color += ('00' + value.toString(16)).substr(-2);
    }
    return color;
  }

  //formats date shown on hover
  private tooltipLabelFormatter(tooltipItem, data):string {
     let date = new Date(tooltipItem.xLabel);
     let label=dateFormat(date, "dddd, mmmm dS, yyyy, h:MM:ss TT");
     return label;

  }
}

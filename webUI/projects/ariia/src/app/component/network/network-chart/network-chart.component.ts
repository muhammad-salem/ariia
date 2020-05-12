import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit, ElementRef } from '@angular/core';
import { Chart } from 'chart.js';
import { DataService } from '../../../service/data.service';
import { SessionHistory } from '../../../model/session-history';
import { Observable, Subscription } from 'rxjs';

@Component({
  selector: 'ariia-network-chart',
  templateUrl: './network-chart.component.html',
  styleUrls: ['./network-chart.component.scss']
})
export class NetworkChartComponent implements OnInit, AfterViewInit, OnDestroy {


  chart: Chart;
  @ViewChild("chart") chartRef: ElementRef;

  historySubscription: Subscription;

  data: SessionHistory[] = [];

  options: any = {
    animation: {
      duration: 0
    },
    scales: {
      scaleOverride : true,
        scaleSteps : 10,
        scaleStepWidth : 50,
        scaleStartValue : 0 ,
      xAxes: [{
        type: 'time',
        time: {
          unit: 'second',
          displayFormats: {second: 'ss'}
        },
        distribution: 'series',
        offset: true,
        bounds: {
          ticks: {
            source: 'auto'
          }
        }
      }],
      yAxes: [{
        gridLines: {
          drawBorder: false
        }
      }]
    }
  };


  constructor(private dataService: DataService) { }


  ngOnInit(): void {
    this.data =  this.dataService.sessionHistory;
  }

  ngAfterViewInit(): void {
    this.chart = new Chart(this.chartRef.nativeElement, {
      type: 'line',
      data: {
				datasets: [{
          label: 'Speed Time Line',
					backgroundColor: 'rgba(255, 99, 132, 0.2)',
					borderColor: 'rgba(75, 192, 192, 1)',
					data: this.data,
					type: 'line',
					pointRadius: 0,
					fill: false,
					lineTension: 0,
					borderWidth: 1
				}]
			},
      options : this.options
    });

    this.historySubscription = this.dataService.historySubject.subscribe(() => {
      this.chart.update();
    });

  }

  ngOnDestroy(): void {
    this.historySubscription.unsubscribe();
    this.chart.destroy();
  }


}

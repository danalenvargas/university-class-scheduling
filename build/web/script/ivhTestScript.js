var app = angular.module('bin', ['ivh.treeview']);

app.controller('DemoCtrl', function() {
  this.stuff = [{
    label: 'Hats',
    children: [
      {label: 'Flat cap'},
      {label: 'Fedora'},
      {label: 'Baseball'},
      {label: 'Top hat'},
      {label: 'Gatsby'}
    ]
  },{
    label: 'Pens',
    selected: true,
    children: [
      {label: 'Fountain'},
      {label: 'Gel ink'},
      {label: 'Roller ball'},
      {label: 'Fiber tip'},
      {label: 'Ballpoint'}
    ]
  },{
    label: 'Whiskey',
    children: [
      {label: 'Irish'},
      {label: 'Scotch'},
      {label: 'Rye'},
      {label: 'Tennessee'},
      {label: 'Bourbon'}
    ]
  }];
})
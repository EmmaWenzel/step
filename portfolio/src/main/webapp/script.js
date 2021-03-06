// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/** Adds a random fact to the page. */
function addRandomFact() {
  const facts =
      ['I have over 20 pairs of sunglasses (that was a weird phase).', 
      'I can still recite most of Hamlet\'s third soliloquy.', 
      'I have 7 pets.', 
      'I can dance a jig.'];

  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

/** 
* Fetches number of comments to load, then fetches comments from the server 
* and adds the requested number to the page 
*/
function loadComments() {
    fetch('/load-comments').then(response => response.text()).then((numToLoad) => {
        fetch('/data').then(response => response.json()).then((comments) => {
            const commentListElement = document.getElementById('comment-list');
            for(i = 0; i < numToLoad && i < comments.length; i++){
                commentListElement.appendChild(createCommentElement(comments[i]));
            }
        });
    });
}

/** Creates a list item that contains the comment */
function createCommentElement(comment){
  
  // create list elements
  const commentElement = document.createElement('li');
  const userCommentElement = document.createElement('div');
  const userNameElement = document.createElement('div');
  const sentimentScoreElement = document.createElement('div');
  const commentSpacing = document.createElement('br');
  
  // deal with anonymous inputs
  if(comment.userName == "" || comment.userName == undefined){
      comment.userName = "Anonymous";
  }
 
  // populate list elements with name and comment
  userCommentElement.innerText = comment.userComment;
  userNameElement.innerText = ("-" + comment.userName);
  score = (comment.score).toFixed(1);
  sentimentScoreElement.innerText = ("Sentiment score: " + score);
  
  // add to list item
  commentElement.appendChild(userCommentElement);
  commentElement.appendChild(userNameElement);
  commentElement.appendChild(sentimentScoreElement);
  commentElement.appendChild(commentSpacing);
  return commentElement;
}


/** Tells the server to delete all comments. */
function deleteComments() {
  fetch('/delete-comments', {method: 'POST'});
  window.location.reload();
}


google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(birdChart);

/** Creates a pie chart and adds it to the page */
function birdChart(){
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Bird');
    data.addColumn('number', 'Species Count');
    data.addRows([
        ['Ducks', 42],
        ['Gulls', 8],
        ['Woodpeckers', 7],
        ['Toucans', 0]
    ]);

    const options = {
        'title': 'Birds by Species Count in Colorado',
        'width': 500,
        'height': 400
    }

    const chart = new google.visualization.PieChart(
      document.getElementById('chart-container'));
    chart.draw(data, options);
}

google.charts.setOnLoadCallback(birdByCountryChart);

/** Creates a geo chart and adds it to the page */
function birdByCountryChart(){
    fetch('/bird-species-data').then(response => response.json())
    .then((birdSpeciesByCountry) => {
        const data = new google.visualization.DataTable();
        data.addColumn('string', 'Country');
        data.addColumn('number', 'Species Count');
        Object.keys(birdSpeciesByCountry).forEach((country) => {
            data.addRow([country, birdSpeciesByCountry[country]]);
        });

        var options = {
            title: "Bird Species Count by Country",
            width: 880,
            height: 586,
            bar: {groupWidth: "95%"},
            legend: { position: "none" },
        };
        var chart = new google.visualization.GeoChart(document.getElementById("geo-chart-container"));
        chart.draw(data, options);
    });
}

google.charts.setOnLoadCallback(voteChart);

/** Fetches bird votes data and uses it to create a chart. */
function voteChart() {
  fetch('/favorite-bird').then(response => response.json())
  .then((birdVotes) => {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Bird');
    data.addColumn('number', 'Votes');
    Object.keys(birdVotes).forEach((bird) => {
      data.addRow([bird, birdVotes[bird]]);
    });

    const options = {
      'title': 'Votes for the Best Bird',
      'width':600,
      'height':500
    };

    const chart = new google.visualization.ColumnChart(
        document.getElementById('vote-chart-container'));
    chart.draw(data, options);
  });
}
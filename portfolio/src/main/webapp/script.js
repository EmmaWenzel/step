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

/** Shows the content of the active tab */
function openTab(tab) {
    let tabcontent;

    // Hide each tab 
    tabcontent = document.getElementsByClassName("tabcontent");
    for (let i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    // Show the current tab
    currentTab = document.getElementById(tab);
    currentTab.style.display = "block";
}

/** 
* Fetches number of comments to load, then fetches comments from the server 
* and adds the requested number to the page 
*/
function loadComments() {
    fetch('/load-comments').then(response => response.text()).then((numToLoad) => {
        fetch('/data').then(response => response.json()).then((comments) => {
            const commentListElement = document.getElementById('comment-list');
            for(i = 0; i < numToLoad; i++){
                comment = createCommentElement(comments[i]);
                if(comment){
                    commentListElement.appendChild(comment);
                }else{
                    numToLoad++;
                }
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
  const commentSpacing = document.createElement('br');
  
  empty = false;
  // deal with empty inputs
  if(comment.userName == "" || comment.userName == undefined){
      comment.userName = "Anonymous";
  }
  if(comment.userComment == ""){
      empty = true;
  } 

  if(!empty){
    // populate list elements with name and comment
    userCommentElement.innerText = comment.userComment;
    userNameElement.innerText = ("-" + comment.userName);
  
    // add to list item
    commentElement.appendChild(userCommentElement);
    commentElement.appendChild(userNameElement);
    commentElement.appendChild(commentSpacing);
    return commentElement;
  }
  return null;
}

/** Tells the server to delete all comments. */
function deleteComments() {
  fetch('/delete-comments', {method: 'POST'});
  window.location.reload();
}

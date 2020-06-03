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

/** Fetches comments from the server and adds them to the page */
async function loadComments() {
    fetch('/data').then(response => response.json()).then((comments) => {
        const commentListElement = document.getElementById('comment-list');
        comments.forEach((comment) => {
          commentListElement.appendChild(createCommentElement(comment))
        })
    });
}

/** Creates a list item that contains the comment */
function createCommentElement(comment){

  const commentElement = document.createElement('li');
  const userCommentElement = document.createElement('span');
  userCommentElement.innerText = comment.userComment;

  commentElement.appendChild(userCommentElement);
  return commentElement;
}

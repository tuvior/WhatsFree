import webapp2
import string
import json
from google.appengine.ext import ndb
from submission import Submission
from vote import Vote
from session import Session

def vote_result(status):
    json_string = {'vote': {'result': status}}
    return json_string

def json_error(option, status, reason):
    json_string = {option: {'status': status, 'reason': reason}}
    return json_string

class submitVote(webapp2.RequestHandler):
    def get(self):
        cookie = self.request.get('cookie')

        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'

        if not cookie:
            error = json_error('vote', 'failure', 'cookie')
            self.response.write(json.dumps(error))

        else:
            id = self.request.get('id')
        
            if not id:
                error = json_error('vote', 'failure', 'no id')
                self.response.write(json.dumps(error))
   
            else:
                session = Session.query(Session.cookie == cookie).get()

                if not session:
                    error = json_error('vote', 'failure', 'session')
                    self.response.write(error)
                else:
                    key = ndb.Key(urlsafe=id)
                    submission = key.get()

                    if not submission:
                        error = json_error('vote', 'failure', 'no submission')
                        self.response.write(error)
                    else:
                        value = self.request.get('value')
                        if not value:
                            error = json_error('vote', 'failure', 'value')
                            self.response.write(error)
                        else:
                            value = int(value)
                            if value < -1 or value > 1:
                                error = json_error('vote', 'failure', 'value')
                                self.response.write(error)
                            else:
                                last_vote = Vote.query(ndb.AND(Vote.user == session.user, Vote.sub_id == id)).get()
                                if not last_vote:
                                    vote_val = 0     
                                else:
                                    vote_val = last_vote.value
                                    last_vote.key.delete()

                                vote_val = value - vote_val;
                                old_score = submission.rating
                                new_score = old_score + vote_val

                                submission.rating = new_score;
                                submission.put()

                                vote = Vote(sub_id = id, value = value, user = session.user)
                                vote.put()

                                response = vote_result('ok')
                                self.response.write(response)
                            
                           



app = webapp2.WSGIApplication([
('/vote', submitVote),
], debug=True)
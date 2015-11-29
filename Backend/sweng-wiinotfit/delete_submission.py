import webapp2
import string
import json
from google.appengine.ext import ndb
from submission import Submission

def json_error(option, status, reason):
    json_string = {option: {'status': status, 'reason': reason}}
    return json_string

class deleteSubmission(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'
        name = self.request.get('name')
        # ev add submitter

        if not name:
            error = json_error('delete submission', 'failure', 'name')
            self.response.write(json.dumps(error))

        else:
            submission = Submission.query(Submission.name == name).get()
            
            if not submission:
            	error = json_error('delete', 'failure', 'no such submission')
                self.response.write(json.dumps(error))

            else:
            	submission.key.delete()
            	response = {'delete submission': {'status': 'ok'}}
            	self.response.write(json.dumps(response))

app = webapp2.WSGIApplication([
    ('/delete/submission', deleteSubmission),
], debug=True)
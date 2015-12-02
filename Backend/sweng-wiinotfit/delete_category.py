import webapp2
import string
import json
from google.appengine.ext import ndb
from submission import Submission

def json_error(option, status, reason):
    json_string = {option: {'status': status, 'reason': reason}}
    return json_string

class deleteCategory(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'
        category = self.request.get('category')

        if not category:
            error = json_error('delete category', 'failure', 'category')
            self.response.write(json.dumps(error))

        else:
            submissions = Submission.query(Submission.category == category).fetch()
            
            if not submissions:
            	error = json_error('delete category', 'failure', 'no such submission')
                self.response.write(json.dumps(error))

            else:
                for i in range(0, len(submissions)):
                    submissions[i].key.delete()

            	response = {'delete category': {'status': 'ok'}}
            	self.response.write(json.dumps(response))

app = webapp2.WSGIApplication([
    ('/delete/category', deleteCategory),
], debug=True)
import webapp2
import string
import json
from google.appengine.ext import ndb
from user import User

def json_error(option, status, reason):
    json_string = {option: {'status': status, 'reason': reason}}
    return json_string

class deleteEntry(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'
        name = self.request.get('name')

        if not name:
            error = json_error('delete', 'failure', 'name')
            self.response.write(json.dumps(error))

        else:
            user = User.query(User.username == name).get()

            if not user:
                error = json_error('delete', 'failure', 'no such user')
                self.response.write(json.dumps(error))

            else:
                user.key.delete()
                response = {'delete': {'status': 'ok'}}
                self.response.write(json.dumps(response))

app = webapp2.WSGIApplication([
    ('/delete', deleteEntry),
], debug=True)
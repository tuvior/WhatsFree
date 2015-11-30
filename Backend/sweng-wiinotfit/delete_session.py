import webapp2
import string
import json
from google.appengine.ext import ndb
from session import Session

def json_error(option, status, reason):
    json_string = {option: {'status': status, 'reason': reason}}
    return json_string

class deleteSession(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'
        cookie = self.request.get('cookie')

        if not cookie:
            error = json_error('delete session', 'failure', 'cookie')
            self.response.write(json.dumps(error))

        else:
            session = Session.query(Session.cookie == cookie).get()
            
            if not session:
            	error = json_error('delete', 'failure', 'no such session')
                self.response.write(json.dumps(error))

            else:
            	session.key.delete()
            	response = {'delete session': {'status': 'ok'}}
            	self.response.write(json.dumps(response))

app = webapp2.WSGIApplication([
    ('/delete/session', deleteSession),
], debug=True)
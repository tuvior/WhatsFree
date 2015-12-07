import webapp2
import string
import random
from google.appengine.ext import ndb
from user import User
from session import Session

def json_error(status, reason):
    json_string = {"login": {'status': status, 'reason': reason}}
    return json_string
    
def login_response(status , cookie):
    json_string = {"login": {'status': status, 'cookie':cookie}}
    return json_string

class Login(webapp2.RequestHandler):
            
    def get(self):
        username = self.request.get("user")
        password = self.request.get("password")
        
        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'
        
        if not username or not password:
            # some fields were missing in the request
            self.response.write(json_error('invalid', ''))
        else:
            # check if username exists in db
            query_user = User.query(User.username == username).fetch()
            if query_user:
                # check if password is correct
                query = User.query(ndb.AND(User.username == username, User.pswd == password)).fetch()
                if query:
                    # generate 64 char session cookie and send it back
                    cookie = ''.join(random.choice(string.ascii_uppercase + string.ascii_lowercase + string.digits) for _ in range(64))
                    session = Session(cookie = cookie, user = username)
                    session.put()
                    self.response.write(login_response('ok', cookie))   
                else: 
                    # password was incorrect
                    self.response.write(json_error('failure', 'password'))
            else:
                # user didn't exist
                self.response.write(json_error('failure', 'user'))

app = webapp2.WSGIApplication([
    ('/login', Login),
], debug=True)
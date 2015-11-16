import webapp2
import string
import random
from google.appengine.ext import ndb
from user import User
from session import Session

def json_response( status , cookie = '' ):
    if status == 0:
        res = """{
    "login": {
        "status": "ok",
        "cookie": \"""" + cookie + """\"
    }
}"""
    elif status == 1:
        res = """{
    "login": {
        "status": "failure",
        "reason": "user"
    }
}"""
    elif status == 2:
        res = """{
    "login": {
        "status": "failure",
        "reason": "password"
    }
}"""
    elif status == -1:
        res = """{
    "login": {
        "status": "invalid"
    }
}"""
    return res

class Login(webapp2.RequestHandler):
            
    def get(self):
        username = self.request.get("user")
        password = self.request.get("password")
        
        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'
        
        if not username or not password:
            # some fields were missing in the request
            self.response.write(json_response(-1))
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
                    self.response.write(json_response(0, cookie))   
                else: 
                    # password was incorrect
                    self.response.write(json_response(2))
            else:
                # user didn't exist
                self.response.write(json_response(1))

app = webapp2.WSGIApplication([
    ('/login', Login),
], debug=True)
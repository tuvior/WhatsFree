import webapp2
from google.appengine.ext import ndb
from user import User

def json_error(status, reason):
    json_string = {"register": {'status': status, 'reason': reason}}
    return json_string
    
def register_response(status):
    json_string = {"register": {'status': status}}
    return json_string
        
class Register(webapp2.RequestHandler):            
    
    def get(self):
        username = self.request.get("user")
        password = self.request.get("password")
        email = self.request.get("email")
       
        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'
         
        if not username or not password or not email:
            # some fields were missing in the request
            self.response.write(json_error('invalid', ''))
        else:
            query_user = User.query(User.username == username).fetch()
            query_email = User.query(User.email == email).fetch()
            if query_user:
                # a user with this username exists already
                self.response.write(json_error('failure', 'username'))
            elif query_email:
                # a user with this email exists already
                self.response.write(json_error('failure', 'email'))
            else: 
                # create a new user with the data recieved
                user = User(username = username, pswd = password, email = email)
                db_result = user.put()
                self.response.write(register_response('ok'))

app = webapp2.WSGIApplication([
    ('/register', Register),
], debug=True)
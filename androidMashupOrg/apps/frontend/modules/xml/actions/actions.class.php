<?php

/**
 * xml actions.
 *
 * @package    mashup
 * @subpackage xml
 * @author     Your name here
 * @version    SVN: $Id: actions.class.php 12479 2008-10-31 10:54:40Z fabien $
 */
class xmlActions extends sfActions {
	/**
	 * Executes index action
	 *
	 * @param sfRequest $request A request object
	 */
	public function executeIndex(sfWebRequest $request) {
		
		$criteria = new Criteria ();
		$criteria->add(IntentPeer::MASHUP,true);
		$mashupIntents = IntentPeer::doSelect ($criteria );
		$applications = ApplicationPeer::doSelect ( new Criteria ( ) );
		
		$xml = "";
		$xml .= '<mashupData databaseId="1" mashupIntentCount="'.count($mashupIntents).'" applicationCount="'.count($applications).'">';

			$xml .= "<mashupIntents>";

				
				foreach ( $mashupIntents as $mashupIntent ) {
					$xml .= '<intent ' .
							'id="'.$mashupIntent->getPrimaryKey().'" ' .
							'action="'.$mashupIntent->getAction().'" '.
							'title="'.$mashupIntent->getTitle().'" '.
							'icon="'.$mashupIntent->getIcon().'" '.
							'>';
						$xml .= '<description>'.$mashupIntent->getDescription().'</description>';
					$xml .= '</intent>';
				}

			$xml .= "</mashupIntents>";
			
			$xml .= "<mashupApplications>";
			
			foreach ( $applications as $application ) {
				$developer = $application->getDeveloper();
				$xml .= '<application ' .
						'name="'.$application->getName().'" ' .
						'package="'.$application->getPackage().'" ' .
						'url="'.$application->getUrl().'" ' .
						'icon="'.$application->getIcon().'" ' .
						'apkUrl="'.$application->getApkUrl().'" ' .
						'developerEmail="'.$developer->getEmail().'" ' .
						'developerUrl="'.$developer->getUrl().'" ' .
						'id="'.$application->getPrimaryKey().'" ' .
						'>';
				$xml .= '<description>sample sample</description>';

				$intents = $application->getIntents();
				foreach ( $intents as $intent ) {
					if($intent->getMashup()){
						$xml .= '<intent action="'.$intent->getAction ().'" ' .
								'id="'.$intent->getPrimaryKey().'" '.
								'mashup="'.$intent->getMashup().
								'" >';
						$xml .= "</intent>";
					}/*
					else{
						$xml .= '<intent ' .
							'action="'.$intent->getAction().'" '.
							'mashup="'.$intent->getMashup().'" '.
							'>';
						$xml .= '<description>'.$intent->getDescription().'</description>';
						$categories = $intent->getCategories();
						foreach ( $categories as $category ) {
							$xml .= "<category>" . $category->getName () . "</category>";
						}
						
						$extras = $intent->getExtras ();
						foreach ( $extras as $extra ) {
							$xml .= '<extra ' .
									'name="' . $extra->getName () . '" ' .
									'type="' . $extra->getExtratype ()->getName () . '" ' .
									'mandatory="' . $extra->isMandatory () . '" ' .
									'description="' . $extra->getExtratype ()->getDescription () . '"' 
									.'/>';
						}
						$xml .= '</intent>';
					}*/
				}
				$xml .= "</application>";
			}
			$xml .= "</mashupApplications>";

		$xml .= "</mashupData>";
		
//		$xml = htmlspecialchars ( $xml );
//		echo "<pre>";
//		echo $xml;
//		die ();
		$this->xml = $xml;
	$response = $this->getResponse();
	$response->setHttpHeader('Content-type','text/xml', true);
	$response->setHttpHeader('charset','utf-8', true);
	
	}
}

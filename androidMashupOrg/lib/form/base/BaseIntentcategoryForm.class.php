<?php

/**
 * Intentcategory form base class.
 *
 * @package    mashup
 * @subpackage form
 * @author     Your name here
 * @version    SVN: $Id: sfPropelFormGeneratedTemplate.php 16976 2009-04-04 12:47:44Z fabien $
 */
class BaseIntentcategoryForm extends BaseFormPropel
{
  public function setup()
  {
    $this->setWidgets(array(
      'id_intentCategory' => new sfWidgetFormInputHidden(),
      'name'              => new sfWidgetFormInput(),
      'description'       => new sfWidgetFormInput(),
    ));

    $this->setValidators(array(
      'id_intentCategory' => new sfValidatorPropelChoice(array('model' => 'Intentcategory', 'column' => 'id_intentCategory', 'required' => false)),
      'name'              => new sfValidatorString(array('max_length' => 45, 'required' => false)),
      'description'       => new sfValidatorString(array('max_length' => 45, 'required' => false)),
    ));

    $this->widgetSchema->setNameFormat('intentcategory[%s]');

    $this->errorSchema = new sfValidatorErrorSchema($this->validatorSchema);

    parent::setup();
  }

  public function getModelName()
  {
    return 'Intentcategory';
  }


}
